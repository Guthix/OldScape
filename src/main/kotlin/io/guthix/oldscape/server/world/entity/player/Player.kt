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
package io.guthix.oldscape.server.world.entity.player

import io.guthix.oldscape.server.event.ScriptCoroutine
import io.guthix.oldscape.server.net.state.game.outp.IfOpensubPacket
import io.guthix.oldscape.server.net.state.game.outp.IfOpentopPacket
import io.guthix.oldscape.server.net.state.game.outp.IfSettext
import io.guthix.oldscape.server.net.state.game.outp.InterestInitPacket
import io.guthix.oldscape.server.world.entity.Entity
import io.guthix.oldscape.server.world.mapsquare.floor
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import io.guthix.oldscape.server.world.mapsquare.zone.tile.tile
import io.netty.channel.ChannelHandlerContext
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.reflect.KProperty

data class Player(
    val index: Int,
    var priority: Int,
    val username: String,
    var ctx: ChannelHandlerContext,
    override val attributes: MutableMap<KProperty<*>, Any?> = mutableMapOf()
) : Entity(attributes), Comparable<Player> {
    val strongQueue = ConcurrentLinkedQueue<ScriptCoroutine>()

    val normalQueue = ConcurrentLinkedQueue<ScriptCoroutine>()

    val weakQueue = ConcurrentLinkedQueue<ScriptCoroutine>()

    val defaultQueue = ConcurrentLinkedQueue<ScriptCoroutine>()

    val position = Tile(0.floor, 3222.tile, 3218.tile)

    var rights = 0

    fun setupInterestManager(worldPlayers: Map<Int, Player>, xteas: List<IntArray>) {
        ctx.write(InterestInitPacket(this, worldPlayers, xteas, position.inZones))
    }

    fun setTopInterface(topInterface: Int) {
        ctx.write(IfOpentopPacket(topInterface))
    }

    fun setSubInterface(parentInterface: Int, slot: Int, childInterface: Int, isClickable: Boolean) {
        ctx.write(IfOpensubPacket(parentInterface, slot, childInterface, isClickable))
    }

    fun setInterfaceText(parentInterface: Int, slot: Int, text: String) {
        ctx.write(IfSettext(parentInterface, slot, text))
    }

    override fun compareTo(other: Player) = when {
        priority < other.priority -> -1
        priority > other.priority -> 1
        else -> 0
    }

    fun handleEvents() {
        for(continuation in weakQueue) {
            continuation.resumeIfPossible()
        }
        ctx.flush()
    }
}