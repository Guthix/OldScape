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
import io.guthix.oldscape.server.world.entity.Monster
import io.guthix.oldscape.server.event.script.Routine
import io.guthix.oldscape.server.pathing.DesinationNpc
import io.guthix.oldscape.server.pathing.breadthFirstSearch
import io.guthix.oldscape.server.world.entity.Sequence
import io.guthix.oldscape.server.world.entity.DamageHitSplat

on(NpcClickEvent::class).where { event.option == "Attack" }.then(Routine.Type.Normal, replace = true) {
    val npc = event.npc
    check(npc is Monster) { "Player $player can't attack NPC $npc because it's not a monster."}
    val destination = DesinationNpc(npc, world.map)
    player.turnToLock(npc)
    player.path = breadthFirstSearch(player.pos, destination, player.size, true, world.map)
    wait{ destination.reached(player.pos.x, player.pos.y, player.size) }
    while(true) { // start combat sequence
        player.animate(Sequence(id = 422))
        npc.hit(DamageHitSplat(10, 0))
        wait(ticks = 3)
    }
}.onCancel {
    player.turnToLock(null)
}