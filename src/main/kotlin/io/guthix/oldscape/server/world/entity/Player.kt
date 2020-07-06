/**
 * This file is part of Guthix OldScape-Server.
 *
 * Guthix OldScape-Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.world.entity

import io.guthix.oldscape.server.blueprints.StanceSequences
import io.guthix.oldscape.server.dimensions.TileUnit
import io.guthix.oldscape.server.dimensions.tiles
import io.guthix.oldscape.server.event.EventHolder
import io.guthix.oldscape.server.event.GameEvent
import io.guthix.oldscape.server.event.PublicMessageEvent
import io.guthix.oldscape.server.net.game.out.*
import io.guthix.oldscape.server.plugin.EventHandler
import io.guthix.oldscape.server.task.Task
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.interest.*
import io.guthix.oldscape.server.world.entity.intface.IfComponent
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
    val stats: StatManager,
    private val energyManager: EnergyManager
) : Character(playerManager.index), Comparable<Player>, EventHolder {
    override val updateFlags = sortedSetOf<PlayerUpdateType>()

    override val events: ConcurrentLinkedQueue<EventHandler<GameEvent>> = ConcurrentLinkedQueue()

    var isLoggingOut: Boolean = false

    val contextMenu: Array<String> get() = contextMenuManager.contextMenu

    var topInterface: TopInterfaceManager = TopInterfaceManager(ctx, id = 165)
        private set

    var nameModifiers: Array<String> = arrayOf("", "", "")

    override var orientation: Int = 0

    val gender: PlayerManager.Gender = PlayerManager.Gender.MALE

    var isSkulled: Boolean = false

    val prayerIcon: Int = -1

    var rights: Int = 2

    val combatLevel: Int = 126

    val style: PlayerManager.Style = PlayerManager.Style(
        hair = 0,
        beard = 10,
        torso = 18,
        arms = 26,
        legs = 36,
        hands = 33,
        feet = 42
    )

    val colours: PlayerManager.Colours = PlayerManager.Colours(0, 0, 0, 0, 0)

    val equipment: PlayerManager.EquipmentSet = PlayerManager.EquipmentSet(
        null, null, null, null, null, null, null, null, null, null, null
    )

    val animations: StanceSequences = StanceSequences(
        stand = 808,
        turn = 823,
        walk = 819,
        turn180 = 820,
        turn90CW = 821,
        turn90CCW = 822,
        run = 824
    )

    override var inRunMode: Boolean = super.inRunMode
        set(value) {
            field = value
            updateFlags.add(PlayerInfoPacket.movementCached)
        }

    override val size: TileUnit = 1.tiles

    var weight: Int
        get() = energyManager.weight
        set(value) {
            energyManager.weight = value
        }

    var energy: Int
        get() = energyManager.energy
        set(value) {
            energyManager.energy = value
        }

    override fun processTasks() {
        while (true) {
            while (events.isNotEmpty()) events.poll().handle()
            val resumed = tasks.values.flatMap { routineList -> routineList.toList().map(Task::run) } // TODO optimize
            if (resumed.all { !it } && events.isEmpty()) break // TODO add live lock detection
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
        stats.initialize(world, this)
        energyManager.initialize(world, this)
    }

    fun synchronize(world: World): List<ChannelFuture> {
        val futures = mutableListOf<ChannelFuture>()
        futures.addAll(topInterface.synchronize(world, this))
        futures.addAll(contextMenuManager.synchronize(world, this))
        futures.addAll(varpManager.synchronize(world, this))
        futures.addAll(stats.synchronize(world, this))
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
        stats.postProcess()
        energyManager.postProcess()
        mapManager.postProcess()
        npcManager.postProcess()
        playerManager.postProcess()
    }

    fun openTopInterface(id: Int, modalSlot: Int? = null, moves: Map<Int, Int> = mutableMapOf()): TopInterfaceManager {
        val movedChildren = mutableMapOf<Int, IfComponent>()
        ctx.write(IfOpentopPacket(id))
        for ((fromSlot, toSlot) in moves) {
            movedChildren[toSlot] = topInterface.children[fromSlot] ?: continue
            ctx.write(IfMovesubPacket(topInterface.id, fromSlot, id, toSlot))
        }
        topInterface.modalSlot?.let { curModalSlot ->
            modalSlot?.let { newModalSlot ->
                if (topInterface.modalOpen && curModalSlot != newModalSlot) {
                    ctx.write(IfMovesubPacket(topInterface.id, curModalSlot, id, newModalSlot))
                }
            }
        }
        topInterface = TopInterfaceManager(ctx, id, topInterface.modalOpen, modalSlot, movedChildren)
        return topInterface
    }

    fun talk(message: PublicMessageEvent) {
        publicMessage = message
        shoutMessage = null
        updateFlags.add(PlayerInfoPacket.chat)
        cancelTasks(ChatTask)
        addTask(ChatTask) {
            wait(ticks = PlayerManager.MESSAGE_DURATION - 1)
            addPostTask { publicMessage = null }
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

    fun equip(legs: LegEquipment?) {
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
        updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun updateVarp(id: Int, value: Int): Unit = varpManager.updateVarp(id, value)

    fun updateVarbit(id: Int, value: Int): Unit = varpManager.updateVarbit(id, value)

    fun runClientScript(id: Int, vararg args: Any) {
        ctx.write(RunclientscriptPacket(id, *args))
    }

    fun setMapFlag(x: TileUnit, y: TileUnit) {
        ctx.write(SetMapFlagPacket(x - mapManager.baseX.inTiles, y - mapManager.baseY.inTiles))
    }

    override fun addOrientationFlag(): Boolean = updateFlags.add(PlayerInfoPacket.orientation)

    override fun addTurnToLockFlag(): Boolean = updateFlags.add(PlayerInfoPacket.lockTurnTo)

    override fun addSequenceFlag(): Boolean = updateFlags.add(PlayerInfoPacket.sequence)

    override fun checkSequenceFlag(): Boolean = updateFlags.contains(PlayerInfoPacket.sequence)

    override fun addSpotAnimationFlag(): Boolean = updateFlags.add(PlayerInfoPacket.spotAnimation)

    override fun addHitUpdateFlag(): Boolean = updateFlags.add(PlayerInfoPacket.hit)

    override fun addShoutFlag(): Boolean = updateFlags.add(PlayerInfoPacket.shout)

    internal fun stageLogout() {
        isLoggingOut = true
        events.clear()
        tasks.clear()
        ctx.writeAndFlush(LogoutFullPacket())
    }

    override fun compareTo(other: Player): Int = when {
        priority < other.priority -> -1
        priority > other.priority -> 1
        else -> 0
    }

    fun clearMap(): Unit = mapManager.clear(this)
}