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
package io.guthix.oldscape.server.playermove

import io.guthix.oldscape.server.event.MapClickEvent
import io.guthix.oldscape.server.event.MiniMapClickEvent
import io.guthix.oldscape.server.pathing.DestinationTile
import io.guthix.oldscape.server.pathing.breadthFirstSearch
import io.guthix.oldscape.server.task.NormalTask
import io.guthix.oldscape.server.world.WorldMap
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.map.Tile
import io.guthix.oldscape.server.world.map.dim.FloorUnit
import io.guthix.oldscape.server.world.map.dim.TileUnit

on(MiniMapClickEvent::class).then {
    player.startWalkingToTile(player.pos.floor, x, y, world.map)
}

on(MapClickEvent::class).then {
    player.startWalkingToTile(player.pos.floor, x, y, world.map)
}

fun Player.startWalkingToTile(floor: FloorUnit, x: TileUnit, y: TileUnit, map: WorldMap) {
    path = breadthFirstSearch(pos, DestinationTile(floor, x, y), size, true, map)
    path.lastOrNull()?.let { dest -> if (dest != Tile(floor, x, y)) setMapFlag(dest.x, dest.y) }
    cancelTasks(NormalTask)
}