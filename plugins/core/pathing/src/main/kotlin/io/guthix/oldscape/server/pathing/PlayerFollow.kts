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

import io.guthix.oldscape.server.event.PlayerClickEvent
import io.guthix.oldscape.server.pathing.algo.DestinationTile
import io.guthix.oldscape.server.pathing.algo.imp.breadthFirstSearch
import io.guthix.oldscape.server.pathing.algo.imp.simplePathSearch
import io.guthix.oldscape.server.routine.NormalAction


on(PlayerClickEvent::class).where { player.contextMenu[event.option - 1] == "Follow" }.then {
    val followed = world.players[event.playerIndex] ?: throw IllegalStateException()
    var dest = DestinationTile(followed.followPosition)
    player.path = breadthFirstSearch(player.position, dest, player.size, true, world.map)
    player.turnToLock(followed)
    player.addRoutine(NormalAction) {
        var currentTarget = player.followPosition
        while(true) {
            if(dest.reached(player.position.x, player.position.y, player.size)) break
            if(currentTarget != player.followPosition) {
                player.path = breadthFirstSearch(player.position, dest, player.size, true, world.map)
            }
            wait(1)
        }
        while(true) {
            wait { currentTarget != player.followPosition }
            dest = DestinationTile(followed.followPosition)
            player.path = simplePathSearch(player.position, dest, player.size, world.map)
            currentTarget = followed.followPosition
        }
    }.onCancel {
        player.turnToLock(null)
    }
}