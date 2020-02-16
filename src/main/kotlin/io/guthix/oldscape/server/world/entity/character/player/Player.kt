/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.world.entity.character.player

import io.guthix.oldscape.cache.config.SequenceConfig
import io.guthix.oldscape.server.api.Varbits
import io.guthix.oldscape.server.event.imp.PublicMessageEvent
import io.guthix.oldscape.server.routine.Routine
import io.guthix.oldscape.server.routine.ConditionalContinuation
import io.guthix.oldscape.server.routine.InitialCondition
import io.guthix.oldscape.server.net.state.game.outp.*
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.WorldMap
import io.guthix.oldscape.server.world.entity.Entity
import io.guthix.oldscape.server.world.entity.Obj
import io.guthix.oldscape.server.world.entity.character.Character
import io.guthix.oldscape.server.world.entity.character.player.interest.MapInterest
import io.guthix.oldscape.server.world.entity.character.player.interest.PlayerInterest
import io.guthix.oldscape.server.world.mapsquare.zone.Zone
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import io.guthix.oldscape.server.world.mapsquare.zone.zones
import io.netty.channel.ChannelHandlerContext
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

data class Player(
    val index: Int,
    var priority: Int,
    override var position: Tile,
    val username: String,
    var ctx: ChannelHandlerContext,
    override val attributes: MutableMap<KProperty<*>, Any?> = mutableMapOf()
) : Character(position, attributes), Comparable<Player> {
    val inEvents = ConcurrentLinkedQueue<() -> Unit>()

    lateinit var clientSettings: ClientSettings

    val routines = TreeMap<Routine.Type, Routine>()

    var inRunMode = false

    var isTeleporting = false

    var rights = 2

    var combatLevel = 126

    val appearance = Appearance(
        gender = Appearance.Gender.MALE,
        isSkulled = false,
        overheadIcon = -1,
        apparel = Appearance.Apparel(
            skinColor = 26,
            head = 0,
            chest = 18,
            hands = 33,
            legs = 36,
            feet = 42,
            weapon = 0,
            shield = 0
        ),
        animations = Appearance.Animations(
            stand = 808,
            turn = 823,
            walk = 819,
            turn180 = 820,
            turn90CW = 821,
            turn90CCW = 822,
            run = 824
        )
    )

    override var orientation: Int by Delegates.observable(0) { _, old, new ->
        if(old != new) updateFlags.add(PlayerInfoPacket.orientation)
    }

    var sequence: Int? by Delegates.observable<Int?>(null) { _, _, _ ->
        updateFlags.add(PlayerInfoPacket.sequence)
    }

    var publicMessage: PublicMessageEvent by Delegates.observable(PublicMessageEvent(0,0, "")) {
        _, _, _ -> updateFlags.add(PlayerInfoPacket.chat)
    }

    var shoutMessage: String by Delegates.observable("") { _, _, _ ->
        updateFlags.add(PlayerInfoPacket.shout)
    }

    val playerInterest = PlayerInterest()

    val mapInterest = MapInterest(this)

    val inventory = Inventory(this, 149, 0, 3, arrayOfNulls(28))

    val varps = mutableMapOf<Int, Int>()

    override val updateFlags = sortedSetOf<PlayerInfoPacket.UpdateType>()

    fun addRoutine(type: Routine.Type, routine: suspend Routine.() -> Unit) {
        val cont = Routine(type, this)
        cont.next = ConditionalContinuation(InitialCondition, routine.createCoroutineUnintercepted(cont, cont))
        routines[type] = cont
    }

    fun initializeInterest(worldMap: WorldMap, worldPlayers: PlayerList, pZone: Zone) {
        playerInterest.initialize(this, worldPlayers)
        mapInterest.initialize(pZone, worldMap)
        val xteas = mapInterest.getInterestedXteas(worldMap)
        ctx.write(InterestInitPacket(this, worldPlayers, xteas, position.x.inZones, position.y.inZones))
    }

    fun playerInterestSync(worldPlayers: PlayerList) {
        ctx.write(PlayerInfoPacket(this, worldPlayers))
    }

    fun updateMap(zone: Zone, xteas: List<IntArray>) {
        ctx.write(RebuildNormalPacket(xteas, zone.x, zone.y))
    }

    fun setTopInterface(topInterface: Int) {
        ctx.write(IfOpentopPacket(topInterface))
    }

    fun setSubInterface(parentInterface: Int, slot: Int, childInterface: Int, isClickable: Boolean) {
        ctx.write(IfOpensubPacket(parentInterface, slot, childInterface, isClickable))
    }

    fun moveSubInterface(fromParent: Int, fromChild: Int, toParent: Int, toChild: Int) {
        ctx.write(IfMovesubPacket(fromParent, fromChild, toParent, toChild))
    }

    fun closeSubInterface(parentInterface: Int, slot: Int) {
        ctx.write(IfClosesubPacket(parentInterface, slot))
    }

    fun setInterfaceText(parentInterface: Int, slot: Int, text: String) {
        ctx.write(IfSettext(parentInterface, slot, text))
    }

    fun updateStat(id: Int, xp: Int, status: Int) {
        ctx.write(UpdateStatPacket(id, xp, status))
    }

    fun updateWeight(amount: Int) {
        ctx.write(UpdateRunweightPacket(amount))
    }

    fun runClientScript(id: Int, vararg args: Any) {
        ctx.write(RunclientscriptPacket(id, *args))
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
        val dx = (position.x.value + (sizeX.value.toDouble() / 2)) - (entity.position.x.value + (entity.sizeX.value.toDouble() / 2))
        val dy = (position.y.value + (sizeY.value.toDouble() / 2)) - (entity.position.y.value + (entity.sizeY.value.toDouble() / 2))
        if (dx.toInt() != 0 || dy.toInt() != 0) {
            orientation = (atan2(dx, dy) * 325.949).toInt() and 0x7FF
        }
    }

    fun addFullInventory(interfaceId: Int, interfacePosition: Int,containerId: Int, objs: List<Obj?>) {
        ctx.write(UpdateInvFullPacket(interfaceId, interfacePosition, containerId, objs))
    }

    fun addPartialInventory(interfaceId: Int, interfacePosition: Int,containerId: Int, objs: Map<Int, Obj?>) {
        ctx.write(UpdateInvPartialPacket(interfaceId, interfacePosition, containerId, objs))
    }

    fun releaseInvMemory(containerId: Int) {
        ctx.write(UpdateInvStopTransmitPacket(containerId))
    }

    fun clearInv(interfaceId: Int, interfacePosition: Int) {
        ctx.write(UpdateInvClearPacket(interfaceId, interfacePosition))
    }

    fun startSequence(seqId: Int) {
        sequence = seqId
    }

    fun stopSequence() {
        sequence = null
    }

    fun shout(message: String) {
        shoutMessage = message
    }

    fun chat(message: PublicMessageEvent) {
        publicMessage = message
    }

    fun syncMapInterest(pZone: Zone, worldMap: WorldMap) {
        mapInterest.checkReload(pZone, worldMap)
        mapInterest.packetCache.forEachIndexed { x, yPacketList ->
            yPacketList.forEachIndexed { y, packetList ->
                if(packetList.size == 1) {
                    ctx.write(UpdateZonePartialFollows(x.zones.inTiles, y.zones.inTiles))
                    ctx.write(packetList.first())
                    packetList.clear()
                } else if(packetList.size > 1){
                    ctx.write(UpdateZonePartialEnclosed(x.zones.inTiles, y.zones.inTiles, packetList.toList()))
                    packetList.clear()
                }
            }
        }
    }

    override fun compareTo(other: Player) = when {
        priority < other.priority -> -1
        priority > other.priority -> 1
        else -> 0
    }

    fun handleEvents() {
        updateFlags.clear()
        while(inEvents.isNotEmpty()) {
            inEvents.poll().invoke()
        }
        val routes = routines.values.toTypedArray().copyOf()
        routes.forEach { it.resumeIfPossible() }
    }

    fun interestSynchronize(world: World) {
        inventory.update()
        val pZone = world.map.getZone(position) ?: throw IllegalStateException("Player is outside of the map.")
        syncMapInterest(pZone, world.map)
        playerInterestSync(world.players)
        ctx.flush()
    }
}