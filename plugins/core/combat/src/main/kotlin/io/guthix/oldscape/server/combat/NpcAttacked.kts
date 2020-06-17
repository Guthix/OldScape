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

import io.guthix.oldscape.server.event.NpcAttackedEvent
import io.guthix.oldscape.server.pathing.DestinationPlayer
import io.guthix.oldscape.server.pathing.simplePathSearch
import io.guthix.oldscape.server.task.NormalTask
import io.guthix.oldscape.server.world.entity.HitMark
import io.guthix.oldscape.server.world.entity.interest.MovementInterestUpdate
import io.guthix.oldscape.server.combat.dmg.calcHit

on(NpcAttackedEvent::class).then {
    if(event.npc.inCombatWith == player) return@then
    var playerDestination = DestinationPlayer(player, world.map)
    event.npc.inCombatWith = player
    event.npc.cancelTasks(NormalTask)
    event.npc.addTask(NormalTask) { // start npc combat
        while (true) {
            event.npc.animate(io.guthix.oldscape.server.world.entity.Sequence(id = 5578))
            val damage = event.npc.calcHit(player) ?: 0
            val hmColor = if (damage == 0) HitMark.Color.BLUE else HitMark.Color.RED
            player.hit(hmColor, damage, 0)
            wait(ticks = 5)
            wait { playerDestination.reached(event.npc.pos.x, event.npc.pos.y, event.npc.size) }
        }
    }
    event.npc.addTask(NormalTask) {
        event.npc.turnToLock(player)
        while (true) {
            wait { player.movementType != MovementInterestUpdate.STAY }
            playerDestination = DestinationPlayer(player, world.map)
            event.npc.path = simplePathSearch(event.npc.pos, playerDestination, event.npc.size, world.map)
            wait(ticks = 1)
        }
    }.onCancel {
        event.npc.inCombatWith = null
        event.npc.turnToLock(null)
    }
}