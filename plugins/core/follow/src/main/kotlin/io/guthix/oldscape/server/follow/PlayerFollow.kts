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
package io.guthix.oldscape.server.follow

import io.guthix.oldscape.server.event.PlayerClickEvent
import io.guthix.oldscape.server.pathing.DestinationTile
import io.guthix.oldscape.server.pathing.breadthFirstSearch
import io.guthix.oldscape.server.pathing.simplePathSearch
import io.guthix.oldscape.server.event.script.NormalTask

on(PlayerClickEvent::class).where { event.option == "Follow" }.then {
    val followed = event.player
    val dest = DestinationTile(followed.followPosition)
    player.path = breadthFirstSearch(player.pos, dest, player.size, true, world.map)
    player.turnToLock(followed)
    val currentTarget = player.followPosition
    player.addTask(NormalTask, replace = true) {
        while(true) {
            if(dest.reached(player.pos.x, player.pos.y, player.size)) break
            if(currentTarget != followed.followPosition) {
                player.path = breadthFirstSearch(player.pos, dest, player.size, true, world.map)
            }
            wait(1)
        }
        while(true) {
            wait { currentTarget != followed.followPosition }
            player.path = simplePathSearch(player.pos, DestinationTile(followed.followPosition), player.size, world.map)
        }
    }.onCancel {
        player.turnToLock(null)
    }
}