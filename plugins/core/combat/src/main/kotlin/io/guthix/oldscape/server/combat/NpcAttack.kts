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
package io.guthix.oldscape.server.combat

import io.guthix.oldscape.server.event.NpcClickEvent
import io.guthix.oldscape.server.event.script.NormalTask
import io.guthix.oldscape.server.pathing.DesinationNpc
import io.guthix.oldscape.server.pathing.breadthFirstSearch
import io.guthix.oldscape.server.world.entity.Sequence
import io.guthix.oldscape.server.world.entity.HitMark

on(NpcClickEvent::class).where { event.option == "Attack" }.then {
    val destination = DesinationNpc(event.npc, world.map)
    player.turnToLock(event.npc)
    player.path = breadthFirstSearch(player.pos, destination, player.size, true, world.map)
    player.addTask(NormalTask, replace = true) {
        wait{ destination.reached(player.pos.x, player.pos.y, player.size) }
        event.npc.addTask(NormalTask, replace = true) { // start npc combat
            event.npc.turnToLock(player)
            while(true) {
                event.npc.animate(Sequence(id = 5578))
                player.hit(HitMark.Colour.RED, 10, 0)
                println("Suspend")
                wait(ticks = 5)
                println("After wait")
            }

        }
        while(true) { // start player combat
            player.animate(Sequence(id = 422))
            event.npc.hit(HitMark.Colour.RED, 10, 0)
            wait(ticks = 4)
        }
    }.onCancel {
        player.turnToLock(null)
    }

}