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
import io.guthix.oldscape.server.event.script.Routine
import io.guthix.oldscape.server.pathing.DesinationNpc
import io.guthix.oldscape.server.pathing.breadthFirstSearch

on(NpcClickEvent::class).where { event.option == "Attack" }.then(Routine.Type.Normal, replace = true) {
    val destination = DesinationNpc(event.npc, world.map)
    player.turnToLock(event.npc)
    player.path = breadthFirstSearch(player.pos, destination, player.size, true, world.map)
}