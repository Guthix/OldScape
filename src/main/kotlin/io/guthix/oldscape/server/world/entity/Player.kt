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

data class Player(
    var priority: Int,
    var ctx: ChannelHandlerContext,
    val clientSettings: ClientSettings,
    val playerManager: PlayerManager,
    internal val npcManager: NpcManager,
    internal val mapManager: MapManager,
    internal val contextMenuManager: ContextMenuManager,
    internal val varpManager: VarpManager,
    internal val statManager: StatManager,
    internal val energyManager: EnergyManager
) : Character(playerManager), Comparable<Player> {
    internal val inEvents = ConcurrentLinkedQueue<Routine>()

    internal val routines = sortedMapOf<Routine.Type, MutableList<Routine>>()

    var isLoggingOut = false

    val contextMenu get() = contextMenuManager.contextMenu

    var topInterface = TopInterfaceManager(ctx, id = 165)
        private set

    var inRunMode get() = playerManager.inRunMode
        set(value) {
            playerManager.inRunMode = value
            playerManager.updateFlags.add(PlayerInfoPacket.movementCached)
        }

    var followPosition get() = playerManager.followPosition
        set(value) { playerManager.followPosition = value }

    var path get() = playerManager.path
        set(value) { playerManager.path = value }

    val equipment get() = playerManager.equipment

    val publicMessage get() = playerManager.publicMessage

    val shoutMessage get() = playerManager.shoutMessage

    val sequence get() = playerManager.sequence

    val spotAnimation get() = playerManager.spotAnimation

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

    fun postProcess() {
        topInterface.postProcess()
        contextMenuManager.postProcess()
        varpManager.postProcess()
        statManager.postProcess()
        energyManager.postProcess()
        mapManager.postProcess()
        npcManager.postProcess()
        playerManager.postProcess()
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
        playerManager.publicMessage = message
        playerManager.shoutMessage = null
        playerManager.updateFlags.add(PlayerInfoPacket.chat)
        addSuspendableRoutine(Routine.Type.Chat, replace = true) {
            wait(ticks = PlayerManager.MESSAGE_DURATION)
            playerManager.publicMessage = null
        }
    }

    fun shout(message: String) {
        playerManager.publicMessage = null
        playerManager.shoutMessage = message
        playerManager.updateFlags.add(PlayerInfoPacket.shout)
        addSuspendableRoutine(Routine.Type.Chat, replace = true) {
            wait(ticks = PlayerManager.MESSAGE_DURATION)
            playerManager.shoutMessage = null
        }
    }

    fun senGameMessage(message: String) {
        ctx.write(MessageGamePacket(0, false, message))
    }

    fun animate(sequence: Sequence) {
        playerManager.sequence = sequence
        playerManager.updateFlags.add(PlayerInfoPacket.sequence)
        addSuspendableRoutine(Routine.Type.Weak) {
            val duration = playerManager.sequence?.duration ?: throw IllegalStateException(
                "Can't start routine because sequence does not exist."
            )
            wait(ticks = duration)
            playerManager.sequence = null
        }
    }

    fun stopAnimation() {
        playerManager.sequence = null
        playerManager.updateFlags.add(PlayerInfoPacket.sequence)
        cancelRoutine(Routine.Type.Weak)
    }

    fun spotAnimate(spotAnimation: SpotAnimation) {
        playerManager.spotAnimation = spotAnimation
        playerManager.updateFlags.add(PlayerInfoPacket.spotAnimation)
        addSuspendableRoutine(Routine.Type.Weak) {
            val duration = playerManager.spotAnimation?.sequence?.duration ?: throw IllegalStateException(
                "Can't start routine because spot animation or sequence does not exist."
            )
            wait(ticks = duration)
            playerManager.spotAnimation = null
        }
    }

    fun stopSpotAnimation() {
        playerManager.spotAnimation = null
        playerManager.updateFlags.add(PlayerInfoPacket.spotAnimation)
        cancelRoutine(Routine.Type.Weak)
    }

    fun equip(head: HeadEquipment?) {
        playerManager.equipment.head = head
        playerManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(cape: CapeEquipment?) {
        playerManager.equipment.cape = cape
        playerManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(neck: NeckEquipment?) {
        playerManager.equipment.neck = neck
        playerManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(ammunition: AmmunitionEquipment?) {
        playerManager.equipment.ammunition = ammunition
        playerManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(weapon: WeaponEquipment?) {
        playerManager.equipment.weapon = weapon
        playerManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(shield: ShieldEquipment?) {
        playerManager.equipment.shield = shield
        playerManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(body: BodyEquipment?) {
        playerManager.equipment.body = body
        playerManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(legs: LegsEquipment?) {
        playerManager.equipment.legs = legs
        playerManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(hands: HandEquipment?) {
        playerManager.equipment.hands = hands
        playerManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(feet: FeetEquipment?) {
        playerManager.equipment.feet = feet
        playerManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(ring: RingEquipment?) {
        playerManager.equipment.ring = ring
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
        playerManager.updateFlags.add(PlayerInfoPacket.orientation)
    }

    fun turnToLock(char: Character?) {
        playerManager.interacting = char
        char?.let { setOrientation(char) }
        println("Turn to lock $char")
        playerManager.updateFlags.add(PlayerInfoPacket.lockTurnToCharacter)
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
        if (dx.toInt() != 0 || dy.toInt() != 0) playerManager.orientation = (atan2(dx, dy) * 325.949).toInt() and 0x7FF
    }

    override fun compareTo(other: Player) = when {
        priority < other.priority -> -1
        priority > other.priority -> 1
        else -> 0
    }

    fun clearMap() = mapManager.clear(this)
}