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

import io.guthix.oldscape.cache.config.VarbitConfig
import io.guthix.oldscape.server.net.state.game.OutGameEvent
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
import io.netty.channel.ChannelHandlerContext
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.math.atan2
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

    val outEvents = mutableListOf<OutGameEvent>()

    lateinit var clientSettings: ClientSettings

    val routines = TreeMap<Routine.Type, Routine>()

    var inRunMode = false

    var isTeleporting = false

    var rights = 0

    val playerInterest = PlayerInterest()

    val mapInterest = MapInterest(this)

    override val updateFlags = mutableSetOf<PlayerInfoPacket.UpdateType>()

    fun addRoutine(type: Routine.Type, routine: suspend Routine.() -> Unit) {
        val cont = Routine(type, this)
        cont.next = ConditionalContinuation(InitialCondition, routine.createCoroutineUnintercepted(cont, cont))
        routines[type] = cont
    }

    fun initializeInterest(worldPlayers: PlayerList, xteas: List<IntArray>) {
        playerInterest.initialize(this, worldPlayers)
        outEvents.add(InterestInitPacket(this, worldPlayers, xteas, position.x.inZones, position.y.inZones))
    }

    fun playerInterestSync(worldPlayers: PlayerList) {
        outEvents.add(PlayerInfoPacket(this, worldPlayers))
    }

    fun updateMap(zone: Zone, xteas: List<IntArray>) {
        outEvents.add(RebuildNormalPacket(xteas, zone.x, zone.y))
    }

    fun setTopInterface(topInterface: Int) {
        outEvents.add(IfOpentopPacket(topInterface))
    }

    fun setSubInterface(parentInterface: Int, slot: Int, childInterface: Int, isClickable: Boolean) {
        outEvents.add(IfOpensubPacket(parentInterface, slot, childInterface, isClickable))
    }

    fun moveSubInterface(fromParent: Int, fromChild: Int, toParent: Int, toChild: Int) {
        outEvents.add(IfMovesubPacket(fromParent, fromChild, toParent, toChild))
    }

    fun closeSubInterface(parentInterface: Int, slot: Int) {
        outEvents.add(IfClosesubPacket(parentInterface, slot))
    }

    fun setInterfaceText(parentInterface: Int, slot: Int, text: String) {
        outEvents.add(IfSettext(parentInterface, slot, text))
    }

    fun updateStat(id: Int, xp: Int, status: Int) {
        outEvents.add(UpdateStatPacket(id, xp, status))
    }

    fun updateWeight(amount: Int) {
        outEvents.add(UpdateRunweightPacket(amount))
    }

    fun runClientScript(id: Int, vararg args: Any) {
        outEvents.add(RunclientscriptPacket(id, *args))
    }

    fun updateVarp(id: Int, value: Int) {
        if (value <= Byte.MIN_VALUE || value >= Byte.MAX_VALUE) {
            outEvents.add(VarpLargePacket(id, value))
        } else {
            outEvents.add(VarpSmallPacket(id, value))
        }
    }

    fun updateVarBit(config: VarbitConfig, value: Int) {
        val rstMask = Int.MAX_VALUE shr config.lsb.toInt() shl config.lsb.toInt() ushr config.msb.toInt() shl config.msb.toInt()
        val bitMask = (value shl config.lsb.toInt()) and (Int.MAX_VALUE shr config.msb.toInt())
        updateVarp(config.id, 0 and rstMask or bitMask)
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
        val baseZoneX = mapInterest.lastLoadedZone.x - MapInterest.RANGE
        val baseZoneY = mapInterest.lastLoadedZone.y - MapInterest.RANGE
        outEvents.add(UpdateZonePartialFollows((zone.x - baseZoneX).inTiles, (zone.y - baseZoneY).inTiles))
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
        outEvents.forEach { ctx.write(it) }
        outEvents.clear()
        ctx.flush()
    }
}