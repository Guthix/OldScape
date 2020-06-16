/**
 * This file is part of Guthix OldScape-Server.
 *
 * Guthix OldScape-Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.world

import io.guthix.oldscape.server.net.login.LoginRequest
import io.guthix.oldscape.server.world.entity.Npc
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.entity.interest.*
import io.guthix.oldscape.server.world.map.Tile
import java.util.*
import kotlin.random.Random

class NpcList(capacity: Int) : Iterable<Npc> {
    private val npcs = arrayOfNulls<Npc>(capacity)

    private val occupiedIndexes = TreeSet<Int>()

    private val freeIndexes = Stack<Int>()

    val size: Int get() = occupiedIndexes.size

    init {
        for (index in capacity downTo 1) freeIndexes.push(index)
    }

    fun create(id: Int, pos: Tile): Npc {
        val index = freeIndexes.pop()
        val npc = Npc(index, id, pos)
        npcs[npc.index] = npc
        occupiedIndexes.add(npc.index)
        return npc
    }

    fun remove(npc: Npc) {
        npcs[npc.index] = null
        occupiedIndexes.remove(npc.index)
        freeIndexes.add(npc.index)
    }

    operator fun get(index: Int): Npc? = npcs[index]

    override fun iterator(): IndexIterator = IndexIterator()

    inner class IndexIterator : Iterator<Npc> {
        var indexIterator: MutableIterator<Int> = occupiedIndexes.iterator()

        override fun hasNext(): Boolean = indexIterator.hasNext()

        override fun next(): Npc = npcs[indexIterator.next()] ?: next()
    }
}

class PlayerList(capacity: Int) : Iterable<Player> {
    private val players = arrayOfNulls<Player>(capacity)

    private val occupiedIndexes = mutableListOf<Int>()

    private val freeIndexes = Stack<Int>()

    private val iterator = PriorityIterator()

    val size: Int get() = occupiedIndexes.size

    init {
        for (index in capacity downTo 1) freeIndexes.push(index)
    }

    fun create(req: LoginRequest): Player {
        val index = freeIndexes.pop()
        val priority = Random.nextInt(occupiedIndexes.size + 1)
        val player = Player(priority, req.ctx, req.username, req.clientSettings, PlayerManager(index),
            NpcManager(), MapManager(), ContextMenuManager(), VarpManager(), StatManager(), EnergyManager()
        )
        players[player.index] = player
        player.priority = priority
        occupiedIndexes.add(priority, player.index)
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

    operator fun get(index: Int): Player? = players[index]

    override fun iterator(): PriorityIterator = PriorityIterator()

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