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

import io.guthix.oldscape.server.api.Varbits
import io.guthix.oldscape.server.routine.Routine
import io.guthix.oldscape.server.routine.ConditionalContinuation
import io.guthix.oldscape.server.routine.InitialCondition
import io.guthix.oldscape.server.net.state.game.outp.*
import io.guthix.oldscape.server.world.entity.Entity
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

    var rights = 0

    val playerInterest = PlayerInterest()

    val mapInterest = MapInterest(this)

    val varps = mutableMapOf<Int, Int>()

    override val updateFlags = mutableSetOf<PlayerInfoPacket.UpdateType>()

    fun addRoutine(type: Routine.Type, routine: suspend Routine.() -> Unit) {
        val cont = Routine(type, this)
        cont.next = ConditionalContinuation(InitialCondition, routine.createCoroutineUnintercepted(cont, cont))
        routines[type] = cont
    }

    fun initializeInterest(worldPlayers: PlayerList, xteas: List<IntArray>) {
        playerInterest.initialize(this, worldPlayers)
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
        println("Sending varp $id, $value")
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
            updateFlags.add(PlayerInfoPacket.orientation)
            orientation = (atan2(dx, dy) * 325.949).toInt() and 0x7FF
        }
    }

    private fun updateZonePartialFollows(zone: Zone) {
        ctx.write(UpdateZonePartialFollows((zone.x - mapInterest.baseX).inTiles, (zone.y - mapInterest.baseY).inTiles))
    }

    fun syncMapInterest() {
        mapInterest.packetCache.forEachIndexed { x, yPacketList ->
            yPacketList.forEachIndexed { y, packetList ->
                if(packetList.size == 1) {
                    println("dropping!")
                    ctx.write(UpdateZonePartialFollows(x.zones.inTiles, y.zones.inTiles))
                    ctx.write(packetList.first())
                }
                packetList.clear()
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
        syncMapInterest() // TODO move this to plugins
        ctx.flush()
    }
}