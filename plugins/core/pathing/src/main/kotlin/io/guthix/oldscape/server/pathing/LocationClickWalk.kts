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

import io.guthix.oldscape.server.event.LocationClickEvent
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import io.guthix.oldscape.server.event.LocationReachedEvent
import io.guthix.oldscape.server.event.script.Routine
import io.guthix.oldscape.server.event.script.EventBus
import io.guthix.oldscape.server.pathing.algo.DestinationLocation
import io.guthix.oldscape.server.pathing.algo.imp.breadthFirstSearch

on(LocationClickEvent::class).then(Routine.Type.NormalAction) {
    val loc = world.map.getLoc(event.id, player.position.floor, event.x, event.y) ?: error(
        "Could not find location at ${Tile(player.position.floor, event.x, event.y)}."
    )
    val destination = DestinationLocation(loc, world.map)
    player.path = breadthFirstSearch(player.position, destination, player.size, true, world.map)
    player.path.lastOrNull()?.let { dest -> player.setMapFlag(dest.x, dest.y) }
    wait{ destination.reached(player.position.x, player.position.y, player.size) }
    EventBus.schedule(LocationReachedEvent(loc), world, player)
}