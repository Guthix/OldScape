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
import io.guthix.oldscape.server.event.NpcAttackedEvent
import io.guthix.oldscape.server.pathing.DestinationRectangleDirect
import io.guthix.oldscape.server.pathing.simplePathSearch
import io.guthix.oldscape.server.task.NormalTask
import io.guthix.oldscape.server.world.entity.HitMark
import io.guthix.oldscape.server.world.entity.Sequence

on(NpcAttackedEvent::class).then {
    if (npc.inCombatWith == player) return@then
    var playerDestination = DestinationRectangleDirect(player, world.map)
    npc.inCombatWith = player
    npc.cancelTasks(NormalTask)
    npc.addTask(NormalTask) { // combat fighting task
        while (true) {
            wait { playerDestination.reached(npc.pos.x, npc.pos.y, npc.size) }
            npc.animate(Sequence(id = npc.combatSequences?.attack ?: -1))
            val damage = npc.calcHit(player) ?: 0
            val hmColor = if (damage == 0) HitMark.Color.BLUE else HitMark.Color.RED
            player.hit(hmColor, damage, 0)
            player.animate(Sequence(id = player.defenceSequence))
            wait(ticks = npc.blueprint.attackSpeed)
        }
    }
    npc.addTask(NormalTask) { // following task
        npc.turnToLock(player)
        while (true) {
            playerDestination = DestinationRectangleDirect(player, world.map)
            npc.path = simplePathSearch(npc.pos, playerDestination, npc.size, world.map)
            wait(ticks = 1)
            wait { player.lastPos != player.pos }
        }
    }.onCancel {
        npc.inCombatWith = null
        npc.turnToLock(null)
    }
}