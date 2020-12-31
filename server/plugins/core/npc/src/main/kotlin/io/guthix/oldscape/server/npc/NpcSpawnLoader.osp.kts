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

import io.guthix.oldscape.dim.floors
import io.guthix.oldscape.dim.tiles
import io.guthix.oldscape.server.event.WorldInitializedEvent
import io.guthix.oldscape.server.readYaml
import io.guthix.oldscape.server.world.map.Tile

on(WorldInitializedEvent::class).then {
    val spawns: Map<String, List<NpcSpawn>> = readYaml("/NpcSpawns.yaml")
    var count = 0
    spawns.values.forEach { spawnList ->
        spawnList.forEach { (id, floor, x, y) ->
            world.createNpc(id, Tile(floor.floors, x.tiles, y.tiles))
            count++
        }
    }
    logger.info { "Loaded $count Npc spawns" }
}