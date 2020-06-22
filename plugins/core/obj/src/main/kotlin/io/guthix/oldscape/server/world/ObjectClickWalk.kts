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

import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.event.ObjectClickEvent
import io.guthix.oldscape.server.event.ObjectReachedEvent
import io.guthix.oldscape.server.pathing.DestinationTile
import io.guthix.oldscape.server.pathing.breadthFirstSearch
import io.guthix.oldscape.server.task.NormalTask

on(ObjectClickEvent::class).then {
    val destination = DestinationTile(player.pos.floor, x, y)
    player.path = breadthFirstSearch(player.pos, destination, player.size, true, world.map)
    player.cancelTasks(NormalTask)
    player.addTask(NormalTask) {
        wait { destination.reached(player.pos.x, player.pos.y, player.size) }
        EventBus.schedule(ObjectReachedEvent(id, x, y, player, world))
    }
}
