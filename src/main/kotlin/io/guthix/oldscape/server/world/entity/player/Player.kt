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
import io.guthix.oldscape.server.net.state.game.OutGameEvent
import io.guthix.oldscape.server.world.mapsquare.floor
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import io.guthix.oldscape.server.world.mapsquare.zone.tile.tile
import io.netty.channel.ChannelHandlerContext
import java.util.concurrent.ConcurrentLinkedQueue

data class Player(val index: Int, var priority: Int, val username: String, val ctx: ChannelHandlerContext) : Comparable<Player> {
    val continuations = ConcurrentLinkedQueue<ScriptCoroutine>()

    val position = Tile(0.floor, 3000.tile, 3000.tile)

    var rights = 0

    fun write(event: OutGameEvent) {
        ctx.write(event)
    }

    override fun compareTo(other: Player) = when {
        priority < other.priority -> -1
        priority > other.priority -> 1
        else -> 0
    }

    fun handleEvents() {
        println("tick")
        for(continuation in continuations) {
            continuation.resumeIfPossible()
        }
    }
}