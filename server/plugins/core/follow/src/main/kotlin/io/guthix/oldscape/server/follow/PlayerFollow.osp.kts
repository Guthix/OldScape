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
package io.guthix.oldscape.server.follow

import io.guthix.oldscape.server.event.PlayerClickEvent
import io.guthix.oldscape.server.pathing.DestinationTile
import io.guthix.oldscape.server.pathing.breadthFirstSearch
import io.guthix.oldscape.server.pathing.simplePathSearch
import io.guthix.oldscape.server.task.NormalTask

on(PlayerClickEvent::class).where { contextMenuEntry == "Follow" }.then {
    val followed = clickedPlayer
    val dest = DestinationTile(followed.followPosition)
    player.path = breadthFirstSearch(player.pos, dest, player.size, true, world)
    player.turnToLock(followed)
    val currentTarget = player.followPosition
    player.cancelTasks(NormalTask)
    player.addTask(NormalTask) {
        while (true) {
            if (dest.reached(player.pos.x, player.pos.y, player.size)) break
            if (currentTarget != followed.followPosition) {
                player.path = breadthFirstSearch(player.pos, dest, player.size, true, world)
            }
            wait(ticks = 1)
        }
        while (true) {
            wait { currentTarget != followed.followPosition }
            player.path = simplePathSearch(player.pos, DestinationTile(followed.followPosition), player.size, world)
        }
    }.finalize {
        player.turnToLock(null)
    }
}