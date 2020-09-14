/*
 * Copyright 2018-2020 Guthix
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
package io.guthix.oldscape.server.npc

import io.guthix.oldscape.server.event.NpcSpawnedEvent
import io.guthix.oldscape.server.pathing.DestinationTile
import io.guthix.oldscape.server.pathing.simplePathSearch
import io.guthix.oldscape.server.task.NormalTask
import io.guthix.oldscape.server.template.wanderRadius
import io.guthix.oldscape.server.world.map.dim.tiles
import kotlin.random.Random

on(NpcSpawnedEvent::class).then {
    npc.wanderRadius.let { wanderRadius ->
        if (npc.wanderRadius == 0.tiles) return@then
        npc.addTask(NormalTask) {
            while (true) {
                wait(ticks = Random.nextInt(20))
                val minX = npc.spawnPos.x - wanderRadius
                val maxX = npc.spawnPos.x + wanderRadius
                val minY = npc.spawnPos.y - wanderRadius
                val maxY = npc.spawnPos.y + wanderRadius
                val walkX = Random.nextInt(minX.value, maxX.value).tiles
                val walkY = Random.nextInt(minY.value, maxY.value).tiles
                val dest = DestinationTile(npc.pos.floor, walkX, walkY)
                npc.path = simplePathSearch(npc.pos, dest, npc.size, world.map)
            }
        }
    }
}