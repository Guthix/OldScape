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
import io.guthix.oldscape.server.api.Varbits
import io.guthix.oldscape.server.event.PublicMessageEvent
import io.guthix.oldscape.server.event.script.*
import io.guthix.oldscape.server.net.game.out.*
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.interest.MapInterestManager
import io.guthix.oldscape.server.world.entity.interest.PlayerInterestManager
import io.guthix.oldscape.server.world.entity.intface.IfComponent
import io.guthix.oldscape.server.world.entity.intface.TopInterface
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import io.guthix.oldscape.server.world.map.Zone
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelHandlerContext
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.atan2
import kotlin.math.pow

data class Player(
    var priority: Int,
    var ctx: ChannelHandlerContext,
    override val visualInterestManager: PlayerInterestManager,
    internal val mapInterestManager: MapInterestManager
) : Character(visualInterestManager), Comparable<Player> {
    internal val inEvents = ConcurrentLinkedQueue<Routine>()

    internal val routines = sortedMapOf<Routine.Type, MutableList<Routine>>()

    lateinit var clientSettings: ClientSettings // TODO pass in through constructor?

    val varps = mutableMapOf<Int, Int>() // TODO create VARP manager

    var contextMenu = arrayOf("Follow", "Trade with", "Report")

    var topInterface = TopInterface(ctx, id = 165)

    val index get() = visualInterestManager.index

    var inRunMode
        get() = visualInterestManager.inRunMode
        set(value) {
            visualInterestManager.inRunMode = value
            visualInterestManager.updateFlags.add(PlayerInfoPacket.movementCached)
        }

    var followPosition
        get() = visualInterestManager.followPosition
        set(value) { visualInterestManager.followPosition = value }

    var path
        get() = visualInterestManager.path
        set(value) { visualInterestManager.path = value }

    val equipment get() = visualInterestManager.equipment

    val publicMessage get() = visualInterestManager.publicMessage

    val shoutMessage get() = visualInterestManager.shoutMessage

    val sequence get() = visualInterestManager.sequence

    val spotAnimation get() = visualInterestManager.spotAnimation

    internal fun processInEvents() {
        while(true) {
            while (inEvents.isNotEmpty()) inEvents.poll().run()
            val resumed = routines.values.flatMap { routineList -> routineList.map { it.run() } }
            if(resumed.all { !it } && inEvents.isEmpty()) break // TODO add live lock detection
        }
    }

    fun initialize(world: World) {
        visualInterestManager.initialize(world, this)
        mapInterestManager.initialize(world, this)
        val xteas = mapInterestManager.getInterestedXteas(world.map)
        ctx.write(InterestInitPacket(this, world.players, xteas, position.x.inZones, position.y.inZones))
        topInterface.initialize(world, this)
    }

    fun synchronize(world: World): List<ChannelFuture> {
        val futures = mutableListOf<ChannelFuture>()
        futures.addAll(topInterface.synchronize(world, this))
        futures.addAll(mapInterestManager.synchronize(world, this))
        futures.addAll(visualInterestManager.synchronize(world, this))
        ctx.flush()
        return futures
    }

    fun postProcess() {
        topInterface.postProcess()
        mapInterestManager.postProcess()
        visualInterestManager.postProcess()
        routines.values.forEach { it.forEach { it.postProcess() } }
    }

    fun openTopInterface(id: Int, modalSlot: Int? = null, moves: Map<Int, Int> = mutableMapOf()): TopInterface {
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
        topInterface = TopInterface(ctx, id, topInterface.modalOpen, modalSlot, movedChildren)
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
        visualInterestManager.publicMessage = message
        visualInterestManager.shoutMessage = null
        visualInterestManager.updateFlags.add(PlayerInfoPacket.chat)
        addSuspendableRoutine(Routine.Type.Chat, replace = true) {
            wait(ticks = PlayerInterestManager.MESSAGE_DURATION)
            visualInterestManager.publicMessage = null
        }
    }

    fun shout(message: String) {
        visualInterestManager.publicMessage = null
        visualInterestManager.shoutMessage = message
        visualInterestManager.updateFlags.add(PlayerInfoPacket.shout)
        addSuspendableRoutine(Routine.Type.Chat, replace = true) {
            wait(ticks = PlayerInterestManager.MESSAGE_DURATION)
            visualInterestManager.shoutMessage = null
        }
    }

    fun animate(sequence: Sequence) {
        visualInterestManager.sequence = sequence
        visualInterestManager.updateFlags.add(PlayerInfoPacket.sequence)
        addSuspendableRoutine(Routine.Type.Weak) {
            val duration = visualInterestManager.sequence?.duration ?: throw IllegalStateException(
                "Can't start routine because sequence does not exist."
            )
            wait(ticks = duration)
            visualInterestManager.sequence = null
        }
    }

    fun stopAnimation() {
        visualInterestManager.sequence = null
        visualInterestManager.updateFlags.add(PlayerInfoPacket.sequence)
        cancelRoutine(Routine.Type.Weak)
    }

    fun spotAnimate(spotAnimation: SpotAnimation) {
        visualInterestManager.spotAnimation = spotAnimation
        visualInterestManager.updateFlags.add(PlayerInfoPacket.spotAnimation)
        addSuspendableRoutine(Routine.Type.Weak) {
            val duration = visualInterestManager.spotAnimation?.sequence?.duration ?: throw IllegalStateException(
                "Can't start routine because spot animation or sequence does not exist."
            )
            wait(ticks = duration)
            visualInterestManager.spotAnimation = null
        }
    }

    fun stopSpotAnimation() {
        visualInterestManager.spotAnimation = null
        visualInterestManager.updateFlags.add(PlayerInfoPacket.spotAnimation)
        cancelRoutine(Routine.Type.Weak)
    }

    fun equip(head: HeadEquipment?) {
        visualInterestManager.equipment.head = head
        visualInterestManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(cape: CapeEquipment?) {
        visualInterestManager.equipment.cape = cape
        visualInterestManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(neck: NeckEquipment?) {
        visualInterestManager.equipment.neck = neck
        visualInterestManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(ammunition: AmmunitionEquipment?) {
        visualInterestManager.equipment.ammunition = ammunition
        visualInterestManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(weapon: WeaponEquipment?) {
        visualInterestManager.equipment.weapon = weapon
        visualInterestManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(shield: ShieldEquipment?) {
        visualInterestManager.equipment.shield = shield
        visualInterestManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(body: BodyEquipment?) {
        visualInterestManager.equipment.body = body
        visualInterestManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(legs: LegsEquipment?) {
        visualInterestManager.equipment.legs = legs
        visualInterestManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(hands: HandsEquipment?) {
        visualInterestManager.equipment.hands = hands
        visualInterestManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(feet: FeetEquipment?) {
        visualInterestManager.equipment.feet = feet
        visualInterestManager.updateFlags.add(PlayerInfoPacket.appearance)
    }

    fun equip(ring: RingEquipment?) {
        visualInterestManager.equipment.ring = ring
    }

    fun updateMap(zone: Zone, xteas: List<IntArray>) {
        ctx.write(RebuildNormalPacket(xteas, zone.x, zone.y))
    }

    fun updateStat(id: Int, xp: Int, status: Int) {
        ctx.write(UpdateStatPacket(id, xp, status))
    }

    fun updateWeight(amount: Int) {
        ctx.write(UpdateRunweightPacket(amount))
    }

    fun updateRunEnergy(energy: Int) {
        ctx.write(UpdateRunenergyPacket(energy))
    }

    fun runClientScript(id: Int, vararg args: Any) {
        ctx.write(RunclientscriptPacket(id, *args))
    }

    fun setMapFlag(x: TileUnit, y: TileUnit) {
        ctx.write(SetMapFlagPacket(x - mapInterestManager.baseX.inTiles, y - mapInterestManager.baseY.inTiles))
    }

    fun updateVarbit(varbitId: Int, value: Int) {
        fun Int.setBits(msb: Int, lsb: Int): Int = this xor ((1 shl (msb + 1)) - 1) xor ((1 shl lsb) - 1)
        @Suppress("INTEGER_OVERFLOW")
        fun Int.clearBits(msb: Int, lsb: Int) =  ((1 shl 4 * 8 - 1) - 1).setBits(msb, lsb) and this

        val config = Varbits[varbitId]
        val bitSize = (config.msb.toInt() - config.lsb.toInt()) + 1
        if(value > 2.0.pow(bitSize) - 1) throw IllegalArgumentException("Value $value to big for this varbit.")
        var curVarp = varps[config.varpId] ?: 0
        curVarp.clearBits(config.msb.toInt(), config.lsb.toInt())
        curVarp = curVarp or value shl config.lsb.toInt()
        varps[config.varpId] = curVarp
        updateVarp(config.varpId, curVarp)
    }


    fun updateVarp(id: Int, value: Int) {
        if (value <= Byte.MIN_VALUE || value >= Byte.MAX_VALUE) {
            ctx.write(VarpLargePacket(id, value))
        } else {
            ctx.write(VarpSmallPacket(id, value))
        }
    }

    fun turnTo(entity: Entity) {
        setOrientation(entity)
        visualInterestManager.updateFlags.add(PlayerInfoPacket.orientation)
    }

    fun turnToLock(char: Character?) {
        visualInterestManager.interacting = char
        char?.let { setOrientation(char) }
    }

    private fun setOrientation(entity: Entity) {
        val dx = (position.x.value + (sizeX.value.toDouble() / 2)) - (entity.position.x.value + (entity.sizeX.value.toDouble() / 2))
        val dy = (position.y.value + (sizeY.value.toDouble() / 2)) - (entity.position.y.value + (entity.sizeY.value.toDouble() / 2))
        if (dx.toInt() != 0 || dy.toInt() != 0) orientation = (atan2(dx, dy) * 325.949).toInt() and 0x7FF
    }

    fun clearInv(interfaceId: Int, interfacePosition: Int) {
        ctx.write(UpdateInvClearPacket(interfaceId, interfacePosition))
    }

    fun senGameMessage(message: String) {
        ctx.write(MessageGamePacket(0, false, message))
    }

    fun updateContextMenu() {
        contextMenu.forEachIndexed { i, text ->
            ctx.write(SetPlayerOpPacket(false, i + 1, text))
        }
    }

    override fun compareTo(other: Player) = when {
        priority < other.priority -> -1
        priority > other.priority -> 1
        else -> 0
    }

    fun clearMap() = mapInterestManager.clear(this)
}