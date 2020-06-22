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
package io.guthix.oldscape.server.playermove

import io.guthix.oldscape.server.dimensions.FloorUnit
import io.guthix.oldscape.server.dimensions.TileUnit
import io.guthix.oldscape.server.event.MapClickEvent
import io.guthix.oldscape.server.event.MiniMapClickEvent
import io.guthix.oldscape.server.pathing.DestinationTile
import io.guthix.oldscape.server.pathing.breadthFirstSearch
import io.guthix.oldscape.server.task.NormalTask
import io.guthix.oldscape.server.world.WorldMap
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.map.Tile

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