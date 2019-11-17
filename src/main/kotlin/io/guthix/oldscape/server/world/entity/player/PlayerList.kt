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
import java.util.Stack
import kotlin.random.Random

class PlayerList(capacity: Int) : Iterable<Player> {
    private val players = arrayOfNulls<Player>(capacity)

    private val freeIndexes = Stack<Int>()

    private val occupiedIndexes = mutableListOf<Int>()

    private val iterator = PriorityIterator()

    private val random = Random.Default

    val size get() = occupiedIndexes.size

    init {
        for (index in capacity downTo 1) freeIndexes.push(index)
    }

    fun create(request: LoginRequest): Player {
        val index = freeIndexes.pop()
        val pid = random.nextInt(occupiedIndexes.size + 1)
        val player = Player(index, pid, request.username, request.ctx)
        occupiedIndexes.add(pid, player.index)
        return player
    }

    fun remove(player: Player) {
        players[player.index] = null
        occupiedIndexes.remove(player.index)
        freeIndexes.add(player.index)
    }

    fun randomizePriority() {
        occupiedIndexes.shuffle()
        for (index in occupiedIndexes) {
            players[index]!!.priority = iterator.currentIndex
        }
    }

    operator fun get(index: Int) = players[index]

    override fun iterator(): Iterator<Player> {
        iterator.currentIndex = 0
        return iterator
    }

    inner class PriorityIterator : MutableIterator<Player> {
        internal var currentIndex = 0

        override fun hasNext(): Boolean = currentIndex < occupiedIndexes.size

        override fun next(): Player = players[occupiedIndexes[currentIndex++]] ?: next()

        override fun remove() {
            players[currentIndex] = null
            occupiedIndexes.remove(currentIndex)
            freeIndexes.add(currentIndex)
        }
    }
}