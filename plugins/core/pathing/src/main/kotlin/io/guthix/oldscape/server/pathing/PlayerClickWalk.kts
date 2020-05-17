/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.pathing

import io.guthix.oldscape.server.event.MapClickEvent
import io.guthix.oldscape.server.event.MiniMapClickEvent
import io.guthix.oldscape.server.pathing.algo.DestinationTile
import io.guthix.oldscape.server.pathing.algo.imp.breadthFirstSearch
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.map.Tile
import io.guthix.oldscape.server.world.WorldMap
import io.guthix.oldscape.server.event.script.Routine
import io.guthix.oldscape.server.dimensions.TileUnit
import io.guthix.oldscape.server.dimensions.FloorUnit

on(MiniMapClickEvent::class).then {
    player.startWalkingToTile(player.pos.floor, event.x, event.y, world.map)
}

on(MapClickEvent::class).then {
    player.startWalkingToTile(player.pos.floor, event.x, event.y, world.map)
}

fun Player.startWalkingToTile(floor: FloorUnit, x: TileUnit, y: TileUnit, map: WorldMap) {
    path = breadthFirstSearch(pos, DestinationTile(floor, x, y), size, true, map)
    path.lastOrNull()?.let { dest -> if(dest != Tile(floor, x, y)) setMapFlag(dest.x, dest.y) }
    cancelRoutine(Routine.Type.Normal)
}