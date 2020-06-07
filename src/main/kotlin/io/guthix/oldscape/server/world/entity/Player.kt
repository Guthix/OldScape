/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.world.entity

import io.guthix.oldscape.server.dimensions.TileUnit
import io.guthix.oldscape.server.dimensions.tiles
import io.guthix.oldscape.server.event.PublicMessageEvent
import io.guthix.oldscape.server.event.script.*
import io.guthix.oldscape.server.net.game.out.*
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.interest.*
import io.guthix.oldscape.server.world.entity.intface.IfComponent
import io.guthix.oldscape.server.world.entity.interest.TopInterfaceManager
import io.guthix.oldscape.server.world.map.Tile
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelHandlerContext
import java.util.concurrent.ConcurrentLinkedQueue

class Player(
    var priority: Int,
    var ctx: ChannelHandlerContext,
    val username: String,
    val clientSettings: ClientSettings,
    private val playerManager: PlayerManager,
    internal val npcManager: NpcManager, //TODO stop exposing
    internal val mapManager: MapManager,
    private val contextMenuManager: ContextMenuManager,
    private val varpManager: VarpManager,
    private val statManager: StatManager,
    private val energyManager: EnergyManager
) : Character(playerManager.index), Comparable<Player> {
    override val updateFlags = sortedSetOf<PlayerUpdateType>()

    internal val inEvents = ConcurrentLinkedQueue<EventHandler<out InGameEvent>>()

    var isLoggingOut = false

    val contextMenu get() = contextMenuManager.contextMenu

    var topInterface = TopInterfaceManager(ctx, id = 165)
        private set

    var nameModifiers = arrayOf("", "", "")

    override var orientation: Int = 0

    var publicMessage: PublicMessageEvent? = null

    val gender = PlayerManager.Gender.MALE

    val isSkulled = false

    val prayerIcon = -1

    var rights = 2

    val combatLevel = 126

    val style = PlayerManager.Style(
        hair = 0,
        beard = 10,
        torso = 18,
        arms = 26,
        legs = 36,
        hands = 33,
        feet = 42
    )

    val colours = PlayerManager.Colours(0, 0, 0, 0, 0)

    val equipment = PlayerManager.Equipment(null, null, null, null, null, null, null, null, null, null, null)

    val animations = PlayerManager.Animations(
        stand = 808,
        turn = 823,
        walk = 819,
        turn180 = 820,
        turn90CW = 821,
        turn90CCW = 822,
        run = 824
    )

    override val size = 1.tiles

    var weight get() = energyManager.weight
        set(value) { energyManager.weight = value }

    var energy get() = energyManager.energy
        set(value) { energyManager.energy = value }

    override fun processTasks() {
        while(true) {
            while (inEvents.isNotEmpty()) inEvents.poll().handle()
            val resumed = tasks.values.flatMap { routineList -> routineList.toList().map { it.run() } } // TODO optimize toList()
            if(resumed.all { !it } && inEvents.isEmpty()) break // TODO add live lock detection
        }
    }

    fun initialize(world: World) {
        playerManager.initialize(world, this)
        mapManager.initialize(world, this)
        val xteas = mapManager.getInterestedXteas(world.map)
        ctx.write(InterestInitPacket(world.players, this, xteas, pos.x.inZones, pos.y.inZones))
        updateFlags.add(PlayerInfoPacket.appearance)
        updateFlags.add(PlayerInfoPacket.orientation)
        updateFlags.add(PlayerInfoPacket.nameModifiers)
        npcManager.initialize(world, this)
        topInterface.initialize(world, this)
        contextMenuManager.initialize(world, this)
        varpManager.initialize(world, this)
        statManager.initialize(world, this)
        energyManager.initialize(world, this)
    }

    fun synchronize(world: World): List<ChannelFuture> {
        val futures = mutableListOf<ChannelFuture>()
        futures.addAll(topInterface.synchronize(world, this))
        futures.addAll(contextMenuManager.synchronize(world, this))
        futures.addAll(varpManager.synchronize(world, this))
        futures.addAll(statManager.synchronize(world, this))
        futures.addAll(energyManager.synchronize(world, this))
        futures.addAll(mapManager.synchronize(world, this))
        futures.addAll(npcManager.synchronize(world, this))
        futures.addAll(playerManager.synchronize(world, this))
        ctx.flush()
        return futures
    }

    override fun postProcess() {
        super.postProcess()
        topInterface.postProcess()
        contextMenuManager.postProcess()
        varpManager.postProcess()
        statManager.postProcess()
        energyManager.postProcess()
        mapManager.postProcess()
        npcManager.postProcess()
        playerManager.postProcess()
    }

    fun openTopInterface(id: Int, modalSlot: Int? = null, moves: Map<Int, Int> = mutableMapOf()): TopInterfaceManager {
        val movedChildren = mutableMapOf<Int, IfComponent>()
        ctx.write(IfOpentopPacket(id))
        for((fromSlot, toSlot) in moves) {
            movedChildren[toSlot] = topInterface.children[fromSlot] ?: continue
            ctx.write(IfMovesubPacket(topInterface.id, fromSlot, id, toSlot))
        }
        topInterface.modalSlot?.let { curModalSlot ->
            modalSlot?.let { newModalSlot ->
                if(topInterface.modalOpen && curModalSlot != newModalSlot) {
                    ctx.write(IfMovesubPacket(topInterface.id, curModalSlot, id, newModalSlot))
                }
            }
        }
        topInterface = TopInterfaceManager(ctx, id, topInterface.modalOpen, modalSlot, movedChildren)
        return topInterface
    }

    private object ChatTask : TaskType

    fun talk(message: PublicMessageEvent) {
        publicMessage = message
        shoutMessage = null
        updateFlags.add(PlayerInfoPacket.chat)
        addTask(ChatTask, replace = true) {
            wait(ticks = PlayerManager.MESSAGE_DURATION)
            publicMessage = null
        }
    }

    fun shout(message: String) { // TODO move this to character
        publicMessage = null
        shoutMessage = message
        updateFlags.add(PlayerInfoPacket.shout)
        addTask(ChatTask, replace = true) {
            wait(ticks = PlayerManager.MESSAGE_DURATION)
            shoutMessage = null
        }
    }

    fun senGameMessage(message: String) {
        ctx.write(MessageGamePacket(0, false, message))
    }

    fun equip(head: HeadEquipment?) {
        equipment.head = head
        updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(cape: CapeEquipment?) {
        equipment.cape = cape
        updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(neck: NeckEquipment?) {
        equipment.neck = neck
        updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(ammunition: AmmunitionEquipment?) {
        equipment.ammunition = ammunition
        updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(weapon: WeaponEquipment?) {
        equipment.weapon = weapon
        updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(shield: ShieldEquipment?) {
        equipment.shield = shield
        updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(body: BodyEquipment?) {
        equipment.body = body
        updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(legs: LegsEquipment?) {
        equipment.legs = legs
        updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(hands: HandEquipment?) {
        equipment.hands = hands
        updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(feet: FeetEquipment?) {
        equipment.feet = feet
        updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(ring: RingEquipment?) {
        equipment.ring = ring
    }

    fun updateVarp(id: Int, value: Int) = varpManager.updateVarp(id, value)

    fun updateVarbit(id: Int, value: Int) = varpManager.updateVarbit(id, value)

    fun runClientScript(id: Int, vararg args: Any) {
        ctx.write(RunclientscriptPacket(id, *args))
    }

    fun setMapFlag(x: TileUnit, y: TileUnit) {
        ctx.write(SetMapFlagPacket(x - mapManager.baseX.inTiles, y - mapManager.baseY.inTiles))
    }

    override fun addOrientationFlag() = updateFlags.add(PlayerInfoPacket.orientation)

    override fun addTurnToLockFlag() = updateFlags.add(PlayerInfoPacket.lockTurnTo)

    override fun addSequenceFlag() = updateFlags.add(PlayerInfoPacket.sequence)

    override fun addSpotAnimationFlag() = updateFlags.add(PlayerInfoPacket.spotAnimation)

    override fun addHitUpdateFlag() = updateFlags.add(PlayerInfoPacket.hit)

    internal fun stageLogout() {
        isLoggingOut = true
        inEvents.clear()
        tasks.clear()
        ctx.writeAndFlush(LogoutFullPacket())
    }

    override fun compareTo(other: Player) = when {
        priority < other.priority -> -1
        priority > other.priority -> 1
        else -> 0
    }

    fun clearMap() = mapManager.clear(this)
}