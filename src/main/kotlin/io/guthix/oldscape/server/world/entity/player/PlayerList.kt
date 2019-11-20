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

import io.guthix.oldscape.server.net.state.login.LoginRequest
import java.util.*
import kotlin.random.Random

class PlayerList(capacity: Int) : Iterable<Player> {
    private val players = mutableListOf<Player>()

    private val freePriorities = mutableListOf<Int>()

    private val freeIndexes = PriorityQueue<Int>(capacity)

    val size get() = players.size

    init {
        for (index in 0 until capacity) {
            freePriorities.add(index)
            freeIndexes.add(index)
        }
        freePriorities.shuffle()
    }

    fun create(request: LoginRequest): Player {
        val index = freeIndexes.poll()
        val priority = freePriorities[random.nextInt(freePriorities.size)]
        val player = Player(index, priority, request.username, request.ctx)
        players.add(index, player)
        return player
    }

    fun remove(player: Player) {
        players.removeAt(player.index)
        freePriorities.add(random.nextInt(freePriorities.size), player.index)
        freeIndexes.add(player.index)
    }

    fun ranomizePriorities() {
        players.shuffle()
        players.forEachIndexed{ priority, player ->
            player.priority = priority
        }
    }

    operator fun get(index: Int): Player? = players[index]

    override fun iterator() = players.iterator()

    companion object {
        private val random = Random.Default
    }
}