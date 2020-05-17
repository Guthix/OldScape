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

data class Player(
    var priority: Int,
    var ctx: ChannelHandlerContext,
    val clientSettings: ClientSettings,
    val visualManager: PlayerManager,
    internal val mapManager: MapManager,
    internal val contextMenuManager: ContextMenuManager,
    internal val varpManager: VarpManager,
    internal val statManager: StatManager,
    internal val energyManager: EnergyManager
) : Character(visualManager), Comparable<Player> {
    internal val inEvents = ConcurrentLinkedQueue<Routine>()

    internal val routines = sortedMapOf<Routine.Type, MutableList<Routine>>()

    val contextMenu get() = contextMenuManager.contextMenu

    var topInterface = TopInterfaceManager(ctx, id = 165)
        private set

    val index get() = visualManager.index

    var inRunMode get() = visualManager.inRunMode
        set(value) {
            visualManager.inRunMode = value
            visualManager.updateFlags.add(PlayerInfoPacket.movementCached)
        }

    var followPosition get() = visualManager.followPosition
        set(value) { visualManager.followPosition = value }

    var path get() = visualManager.path
        set(value) { visualManager.path = value }

    val equipment get() = visualManager.equipment

    val publicMessage get() = visualManager.publicMessage

    val shoutMessage get() = visualManager.shoutMessage

    val sequence get() = visualManager.sequence

    val spotAnimation get() = visualManager.spotAnimation

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
        visualManager.initialize(world, this)
        mapManager.initialize(world, this)
        val xteas = mapManager.getInterestedXteas(world.map)
        ctx.write(InterestInitPacket(world.players, this, xteas, position.x.inZones, position.y.inZones))
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
        futures.addAll(visualManager.synchronize(world, this))
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
        visualManager.postProcess()
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
        routines.remove(type)
    }

    fun talk(message: PublicMessageEvent) {
        visualManager.publicMessage = message
        visualManager.shoutMessage = null
        visualManager.updateFlags.add(PlayerInfoPacket.chat)
        addSuspendableRoutine(Routine.Type.Chat, replace = true) {
            wait(ticks = PlayerManager.MESSAGE_DURATION)
            visualManager.publicMessage = null
        }
    }

    fun shout(message: String) {
        visualManager.publicMessage = null
        visualManager.shoutMessage = message
        visualManager.updateFlags.add(PlayerInfoPacket.shout)
        addSuspendableRoutine(Routine.Type.Chat, replace = true) {
            wait(ticks = PlayerManager.MESSAGE_DURATION)
            visualManager.shoutMessage = null
        }
    }

    fun senGameMessage(message: String) {
        ctx.write(MessageGamePacket(0, false, message))
    }

    fun animate(sequence: Sequence) {
        visualManager.sequence = sequence
        visualManager.updateFlags.add(PlayerInfoPacket.sequence)
        addSuspendableRoutine(Routine.Type.Weak) {
            val duration = visualManager.sequence?.duration ?: throw IllegalStateException(
                "Can't start routine because sequence does not exist."
            )
            wait(ticks = duration)
            visualManager.sequence = null
        }
    }

    fun stopAnimation() {
        visualManager.sequence = null
        visualManager.updateFlags.add(PlayerInfoPacket.sequence)
        cancelRoutine(Routine.Type.Weak)
    }

    fun spotAnimate(spotAnimation: SpotAnimation) {
        visualManager.spotAnimation = spotAnimation
        visualManager.updateFlags.add(PlayerInfoPacket.spotAnimation)
        addSuspendableRoutine(Routine.Type.Weak) {
            val duration = visualManager.spotAnimation?.sequence?.duration ?: throw IllegalStateException(
                "Can't start routine because spot animation or sequence does not exist."
            )
            wait(ticks = duration)
            visualManager.spotAnimation = null
        }
    }

    fun stopSpotAnimation() {
        visualManager.spotAnimation = null
        visualManager.updateFlags.add(PlayerInfoPacket.spotAnimation)
        cancelRoutine(Routine.Type.Weak)
    }

    fun equip(head: HeadEquipment?) {
        visualManager.equipment.head = head
        visualManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(cape: CapeEquipment?) {
        visualManager.equipment.cape = cape
        visualManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(neck: NeckEquipment?) {
        visualManager.equipment.neck = neck
        visualManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(ammunition: AmmunitionEquipment?) {
        visualManager.equipment.ammunition = ammunition
        visualManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(weapon: WeaponEquipment?) {
        visualManager.equipment.weapon = weapon
        visualManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(shield: ShieldEquipment?) {
        visualManager.equipment.shield = shield
        visualManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(body: BodyEquipment?) {
        visualManager.equipment.body = body
        visualManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(legs: LegsEquipment?) {
        visualManager.equipment.legs = legs
        visualManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(hands: HandEquipment?) {
        visualManager.equipment.hands = hands
        visualManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(feet: FeetEquipment?) {
        visualManager.equipment.feet = feet
        visualManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(ring: RingEquipment?) {
        visualManager.equipment.ring = ring
    }

    fun updateVarbit(id: Int, value: Int) = varpManager.updateVarbit(id, value)

    fun runClientScript(id: Int, vararg args: Any) {
        ctx.write(RunclientscriptPacket(id, *args))
    }

    fun setMapFlag(x: TileUnit, y: TileUnit) {
        ctx.write(SetMapFlagPacket(x - mapManager.baseX.inTiles, y - mapManager.baseY.inTiles))
    }

    fun turnTo(entity: Entity) {
        setOrientation(entity)
        visualManager.updateFlags.add(PlayerInfoPacket.orientation)
    }

    fun turnToLock(char: Character?) {
        visualManager.interacting = char
        char?.let { setOrientation(char) }
    }

    private fun setOrientation(entity: Entity) {
        val dx = (position.x.value + (sizeX.value.toDouble() / 2)) -
            (entity.position.x.value + (entity.sizeX.value.toDouble() / 2))
        val dy = (position.y.value + (sizeY.value.toDouble() / 2)) -
            (entity.position.y.value + (entity.sizeY.value.toDouble() / 2))
        if (dx.toInt() != 0 || dy.toInt() != 0) orientation = (atan2(dx, dy) * 325.949).toInt() and 0x7FF
    }

    override fun compareTo(other: Player) = when {
        priority < other.priority -> -1
        priority > other.priority -> 1
        else -> 0
    }

    fun clearMap() = mapManager.clear(this)
}