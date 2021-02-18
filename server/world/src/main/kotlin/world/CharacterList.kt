/*
 * Copyright 2018-2021 Guthix
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.guthix.oldscape.server.world

import io.guthix.oldscape.server.net.login.LoginRequest
import io.guthix.oldscape.server.world.entity.Npc
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.entity.interest.*
import io.guthix.oldscape.server.world.map.Tile
import io.guthix.oldscape.server.world.map.Zone
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

    fun create(id: Int, pos: Tile, zone: Zone): Npc {
        val index = freeIndexes.pop()
        val npc = Npc(id, index, pos, zone)
        npcs[npc.index] = npc
        occupiedIndexes.add(npc.index)
        return npc
    }

    fun add(npc: Npc) {
        npcs[npc.index] = npc
        occupiedIndexes.add(npc.index)
    }

    fun remove(npc: Npc) {
        npcs[npc.index] = null
        occupiedIndexes.remove(npc.index)
    }

    fun free(npc: Npc) {
        npcs[npc.index] = null
        occupiedIndexes.remove(npc.index)
        freeIndexes.add(npc.index)
    }

    operator fun get(index: Int): Npc? = npcs[index]

    override fun iterator(): IndexIterator = IndexIterator()

    inner class IndexIterator : MutableIterator<Npc> {
        internal var currentIndex = 0

        override fun hasNext(): Boolean = currentIndex < occupiedIndexes.size

        override fun next(): Npc = npcs[currentIndex++] ?: next()

        override fun remove() {
            npcs[currentIndex] = null
            occupiedIndexes.remove(currentIndex)
            freeIndexes.add(currentIndex)
        }
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

    fun create(uid: Int, properties: MutableMap<String, Any>, zone: Zone, req: LoginRequest): Player {
        val index = freeIndexes.pop()
        val priority = Random.nextInt(occupiedIndexes.size + 1)
        val player = Player(
            uid, priority, req.ctx, req.username, req.clientSettings, zone, properties,
            PlayerManager(index), NpcManager(), SceneManager(), EnergyManager(), ContextMenuManager(), VarpManager(),
            StatManager(), TopInterfaceManager(req.ctx, id = 165)
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