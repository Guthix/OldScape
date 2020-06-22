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
package io.guthix.oldscape.server.npc

import io.guthix.oldscape.server.dimensions.tiles
import io.guthix.oldscape.server.event.NpcSpawnedEvent
import io.guthix.oldscape.server.pathing.DestinationTile
import io.guthix.oldscape.server.pathing.simplePathSearch
import io.guthix.oldscape.server.task.NormalTask
import kotlin.random.Random

on(NpcSpawnedEvent::class).then {
    if (npc.wanderRadius == 0.tiles) return@then
    npc.addTask(NormalTask) {
        while (true) {
            wait(ticks = Random.nextInt(20))
            val minX = npc.spawnPos.x - npc.wanderRadius
            val maxX = npc.spawnPos.x + npc.wanderRadius
            val minY = npc.spawnPos.y - npc.wanderRadius
            val maxY = npc.spawnPos.y + npc.wanderRadius
            val walkX = Random.nextInt(minX.value, maxX.value).tiles
            val walkY = Random.nextInt(minY.value, maxY.value).tiles
            val dest = DestinationTile(npc.pos.floor, walkX, walkY)
            npc.path = simplePathSearch(npc.pos, dest, npc.size, world.map)
        }
    }
}