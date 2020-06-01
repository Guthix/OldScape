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
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelHandlerContext
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.atan2

class Player(
    var priority: Int,
    var ctx: ChannelHandlerContext,
    val clientSettings: ClientSettings,
    val playerVisual: PlayerVisual,
    private val playerManager: PlayerManager,
    internal val npcManager: NpcManager, //TODO stop exposing
    internal val mapManager: MapManager,
    private val contextMenuManager: ContextMenuManager,
    private val varpManager: VarpManager,
    private val statManager: StatManager,
    private val energyManager: EnergyManager
) : Character(playerManager.index, playerVisual), Comparable<Player> {
    internal val inEvents = ConcurrentLinkedQueue<Routine>()

    internal val routines = sortedMapOf<Routine.Type, MutableList<Routine>>()

    var isLoggingOut = false

    val contextMenu get() = contextMenuManager.contextMenu

    var topInterface = TopInterfaceManager(ctx, id = 165)
        private set

    var inRunMode get() = playerVisual.inRunMode
        set(value) {
            playerVisual.inRunMode = value
            playerVisual.updateFlags.add(PlayerInfoPacket.movementCached)
        }

    var followPosition get() = playerVisual.followPosition
        set(value) { playerVisual.followPosition = value }

    var path get() = playerVisual.path
        set(value) { playerVisual.path = value }

    val equipment get() = playerVisual.equipment

    val publicMessage get() = playerVisual.publicMessage

    val shoutMessage get() = playerVisual.shoutMessage

    val sequence get() = playerVisual.sequence

    val spotAnimation get() = playerVisual.spotAnimation

    override val size = 1.tiles

    var weight get() = energyManager.weight
        set(value) { energyManager.weight = value }

    var energy get() = energyManager.energy
        set(value) { energyManager.energy = value }

    internal fun processInEvents() {
        while(true) {
            while (inEvents.isNotEmpty()) inEvents.poll().run()
            val resumed = routines.values.flatMap { routineList -> routineList.map { it.run() } }
            if(resumed.all { !it } && inEvents.isEmpty()) break // TODO add live lock detection
        }
    }

    fun initialize(world: World) {
        playerManager.initialize(world, this)
        mapManager.initialize(world, this)
        val xteas = mapManager.getInterestedXteas(world.map)
        ctx.write(InterestInitPacket(world.players, this, xteas, pos.x.inZones, pos.y.inZones))
        playerVisual.initialize(world, this)
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
        futures.addAll(playerVisual.synchronize(world, this))
        ctx.flush()
        return futures
    }

    fun postProcess() {
        topInterface.postProcess()
        contextMenuManager.postProcess()
        varpManager.postProcess()
        statManager.postProcess()
        energyManager.postProcess()
        mapManager.postProcess()
        npcManager.postProcess()
        playerManager.postProcess()
        playerVisual.postProcess()
        routines.values.forEach { it.forEach { routine -> routine.postProcess() } }
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

    fun addSuspendableRoutine(type: Routine.Type, replace: Boolean = false, r: suspend SuspendableRoutine.() -> Unit) {
        val routine = SuspendableRoutine(type, this)
        routine.next = ConditionalContinuation(TrueCondition, r.createCoroutineUnintercepted(routine, routine))
        if(replace) {
            val toRemove = routines.remove(type)
            toRemove?.forEach { it.cancel() }
            routines[type] = mutableListOf<Routine>(routine)
        } else {
            routines.getOrPut(type) { mutableListOf() }.add(routine)
        }
    }

    fun cancelRoutine(type: Routine.Type) {
        routines[type]?.forEach { it.cancel() }
    }

    fun talk(message: PublicMessageEvent) {
        playerVisual.publicMessage = message
        playerVisual.shoutMessage = null
        playerVisual.updateFlags.add(PlayerInfoPacket.chat)
        addSuspendableRoutine(Routine.Type.Chat, replace = true) {
            wait(ticks = PlayerManager.MESSAGE_DURATION)
            playerVisual.publicMessage = null
        }
    }

    fun shout(message: String) {
        playerVisual.publicMessage = null
        playerVisual.shoutMessage = message
        playerVisual.updateFlags.add(PlayerInfoPacket.shout)
        addSuspendableRoutine(Routine.Type.Chat, replace = true) {
            wait(ticks = PlayerManager.MESSAGE_DURATION)
            playerVisual.shoutMessage = null
        }
    }

    fun senGameMessage(message: String) {
        ctx.write(MessageGamePacket(0, false, message))
    }

    fun animate(sequence: Sequence) {
        playerVisual.sequence = sequence
        playerVisual.updateFlags.add(PlayerInfoPacket.sequence)
        addSuspendableRoutine(Routine.Type.Weak) {
            val duration = playerVisual.sequence?.duration ?: throw IllegalStateException(
                "Can't start routine because sequence does not exist."
            )
            wait(ticks = duration)
            playerVisual.sequence = null
        }
    }

    fun stopAnimation() {
        playerVisual.sequence = null
        playerVisual.updateFlags.add(PlayerInfoPacket.sequence)
        cancelRoutine(Routine.Type.Weak)
    }

    fun spotAnimate(spotAnimation: SpotAnimation) {
        playerVisual.spotAnimation = spotAnimation
        playerVisual.updateFlags.add(PlayerInfoPacket.spotAnimation)
        addSuspendableRoutine(Routine.Type.Weak) {
            val duration = playerVisual.spotAnimation?.sequence?.duration ?: throw IllegalStateException(
                "Can't start routine because spot animation or sequence does not exist."
            )
            wait(ticks = duration)
            playerVisual.spotAnimation = null
        }
    }

    fun stopSpotAnimation() {
        playerVisual.spotAnimation = null
        playerVisual.updateFlags.add(PlayerInfoPacket.spotAnimation)
        cancelRoutine(Routine.Type.Weak)
    }

    fun equip(head: HeadEquipment?) {
        playerVisual.equipment.head = head
        playerVisual.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(cape: CapeEquipment?) {
        playerVisual.equipment.cape = cape
        playerVisual.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(neck: NeckEquipment?) {
        playerVisual.equipment.neck = neck
        playerVisual.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(ammunition: AmmunitionEquipment?) {
        playerVisual.equipment.ammunition = ammunition
        playerVisual.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(weapon: WeaponEquipment?) {
        playerVisual.equipment.weapon = weapon
        playerVisual.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(shield: ShieldEquipment?) {
        playerVisual.equipment.shield = shield
        playerVisual.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(body: BodyEquipment?) {
        playerVisual.equipment.body = body
        playerVisual.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(legs: LegsEquipment?) {
        playerVisual.equipment.legs = legs
        playerVisual.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(hands: HandEquipment?) {
        playerVisual.equipment.hands = hands
        playerVisual.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(feet: FeetEquipment?) {
        playerVisual.equipment.feet = feet
        playerVisual.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(ring: RingEquipment?) {
        playerVisual.equipment.ring = ring
    }

    fun updateVarp(id: Int, value: Int) = varpManager.updateVarp(id, value)

    fun updateVarbit(id: Int, value: Int) = varpManager.updateVarbit(id, value)

    fun runClientScript(id: Int, vararg args: Any) {
        ctx.write(RunclientscriptPacket(id, *args))
    }

    fun setMapFlag(x: TileUnit, y: TileUnit) {
        ctx.write(SetMapFlagPacket(x - mapManager.baseX.inTiles, y - mapManager.baseY.inTiles))
    }

    fun turnTo(entity: Entity) {
        setOrientation(entity)
        playerVisual.updateFlags.add(PlayerInfoPacket.orientation)
    }

    fun turnToLock(char: Character?) {
        playerVisual.interacting = char
        char?.let { setOrientation(char) }
        playerVisual.updateFlags.add(PlayerInfoPacket.lockTurnToCharacter)
    }

    internal fun stageLogout() {
        isLoggingOut = true
        inEvents.clear()
        routines.clear()
        ctx.writeAndFlush(LogoutFullPacket())
    }

    private fun setOrientation(entity: Entity) {
        val dx = (pos.x.value + (sizeX.value.toDouble() / 2)) -
            (entity.pos.x.value + (entity.sizeX.value.toDouble() / 2))
        val dy = (pos.y.value + (sizeY.value.toDouble() / 2)) -
            (entity.pos.y.value + (entity.sizeY.value.toDouble() / 2))
        if (dx.toInt() != 0 || dy.toInt() != 0) playerVisual.orientation = (atan2(dx, dy) * 325.949).toInt() and 0x7FF
    }



    override fun compareTo(other: Player) = when {
        priority < other.priority -> -1
        priority > other.priority -> 1
        else -> 0
    }

    fun clearMap() = mapManager.clear(this)
}