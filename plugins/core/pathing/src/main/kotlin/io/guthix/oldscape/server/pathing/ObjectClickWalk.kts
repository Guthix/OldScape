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

import io.guthix.oldscape.server.event.ObjectClickEvent
import io.guthix.oldscape.server.event.ObjectReachedEvent
import io.guthix.oldscape.server.pathing.algo.DestinationTile
import io.guthix.oldscape.server.pathing.algo.imp.breadthFirstSearch
import io.guthix.oldscape.server.event.script.Routine
import io.guthix.oldscape.server.event.script.EventBus

on(ObjectClickEvent::class).then(Routine.Type.NormalAction) {
    val destination = DestinationTile(player.position.floor, event.x, event.y)
    player.visualInterestManager.path = breadthFirstSearch(player.position, destination, player.size, true, world.map)
    wait{ destination.reached(player.position.x, player.position.y, player.size) }
    EventBus.schedule(ObjectReachedEvent(event.id, event.x, event.y), world, player)
}
