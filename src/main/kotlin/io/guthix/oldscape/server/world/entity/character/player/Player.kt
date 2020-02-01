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

import io.guthix.oldscape.server.routine.Routine
import io.guthix.oldscape.server.routine.ConditionalContinuation
import io.guthix.oldscape.server.routine.InitialCondition
import io.guthix.oldscape.server.net.state.game.outp.*
import io.guthix.oldscape.server.world.entity.character.Character
import io.guthix.oldscape.server.world.entity.character.player.interest.MapInterest
import io.guthix.oldscape.server.world.entity.character.player.interest.PlayerInterest
import io.guthix.oldscape.server.world.mapsquare.floors
import io.guthix.oldscape.server.world.mapsquare.zone.Zone
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import io.guthix.oldscape.server.world.mapsquare.zone.tile.tiles
import io.netty.channel.ChannelHandlerContext
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.reflect.KProperty

data class Player(
    val index: Int,
    var priority: Int,
    override var position: Tile,
    val username: String,
    var ctx: ChannelHandlerContext,
    override val attributes: MutableMap<KProperty<*>, Any?> = mutableMapOf()
) : Character(position, attributes), Comparable<Player> {
    val events = ConcurrentLinkedQueue<() -> Unit>()

    lateinit var clientSettings: ClientSettings

    val routines = PriorityQueue<Routine>()

    var inRunMode = false

    var isTeleporting = false

    var rights = 0

    val playerInterest = PlayerInterest()

    val mapInterest = MapInterest(this)

    override val updateFlags = mutableSetOf<PlayerInfoPacket.UpdateType>()

    fun addRoutine(type: Routine.Type, routine: suspend Routine.() -> Unit) {
        val cont = Routine(type, this)
        cont.next = ConditionalContinuation(InitialCondition, routine.createCoroutineUnintercepted(cont, cont))
        routines.add(cont)
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

    override fun compareTo(other: Player) = when {
        priority < other.priority -> -1
        priority > other.priority -> 1
        else -> 0
    }

    fun handleEvents() {
        updateFlags.clear()
        while(events.isNotEmpty()) {
            events.poll().invoke()
        }
        PriorityQueue<Routine>(routines).forEach { it.resumeIfPossible() }
        ctx.flush()
    }
}