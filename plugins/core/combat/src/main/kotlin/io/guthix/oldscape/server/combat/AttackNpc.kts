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
package io.guthix.oldscape.server.combat

import io.guthix.oldscape.server.combat.dmg.calcHit
import io.guthix.oldscape.server.combat.dmg.maxMeleeHit
import io.guthix.oldscape.server.event.NpcClickEvent
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.event.NpcAttackedEvent
import io.guthix.oldscape.server.task.NormalTask
import io.guthix.oldscape.server.pathing.DesinationNpc
import io.guthix.oldscape.server.pathing.breadthFirstSearch
import io.guthix.oldscape.server.world.entity.HitMark
import io.guthix.oldscape.server.world.entity.Sequence

on(NpcClickEvent::class).where { event.option == "Attack" }.then {
    if(player.inCombatWith == event.npc) return@then
    val npcDestination = DesinationNpc(event.npc, world.map)
    player.turnToLock(event.npc)
    player.path = breadthFirstSearch(player.pos, npcDestination, player.size, true, world.map)
    player.inCombatWith = event.npc
    player.cancelTasks(NormalTask)
    player.addTask(NormalTask) {
        wait { npcDestination.reached(player.pos.x, player.pos.y, player.size) }
        EventBus.schedule(NpcAttackedEvent(event.npc), player, world)
        while (true) { // start player combat
            player.animate(Sequence(id = player.combatSequences.attack))
            val damage = player.calcHit(event.npc, player.maxMeleeHit()) ?: 0
            val hmColor = if (damage == 0) HitMark.Color.BLUE else HitMark.Color.RED
            event.npc.hit(hmColor, damage, 0)
            event.npc.animate(Sequence(id = event.npc.combatSequences?.defence ?: -1))
            wait(ticks = player.attackSpeed)
        }
    }.onCancel {
        player.inCombatWith = null
        player.turnToLock(null)
    }
}