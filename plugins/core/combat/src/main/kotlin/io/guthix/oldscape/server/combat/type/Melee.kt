/*
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
 * along with Guthix OldScape-Server. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.combat.type

import io.guthix.oldscape.server.combat.attackSequence
import io.guthix.oldscape.server.combat.attackSpeed
import io.guthix.oldscape.server.combat.combatSequences
import io.guthix.oldscape.server.combat.dmg.calcHit
import io.guthix.oldscape.server.combat.dmg.maxMeleeHit
import io.guthix.oldscape.server.combat.inCombatWith
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.event.NpcAttackedEvent
import io.guthix.oldscape.server.pathing.DestinationRectangleDirect
import io.guthix.oldscape.server.pathing.breadthFirstSearch
import io.guthix.oldscape.server.plugin.ConfigDataMissingException
import io.guthix.oldscape.server.task.NormalTask
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.HitMark
import io.guthix.oldscape.server.world.entity.Npc
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.entity.Sequence

fun Player.meleeAttack(npc: Npc, world: World) {
    val npcDestination = DestinationRectangleDirect(npc, world.map)
    path = breadthFirstSearch(pos, npcDestination, size, true, world.map)
    inCombatWith = npc
    cancelTasks(NormalTask)
    val player = this
    addTask(NormalTask) {
        wait { npcDestination.reached(pos.x, pos.y, size) }
        EventBus.schedule(NpcAttackedEvent(npc, player, world))
        while (true) { // start player combat
            animate(Sequence(id = attackSequence))
            val damage = calcHit(npc, maxMeleeHit()) ?: 0
            val hmColor = if (damage == 0) HitMark.Color.BLUE else HitMark.Color.RED
            npc.hit(hmColor, damage, 0)
            npc.animate(Sequence(id = npc.combatSequences?.defence
                ?: throw ConfigDataMissingException("No block animation for npc $npc.")
            ))
            wait(ticks = attackSpeed)
        }
    }.onCancel {
        inCombatWith = null
        turnToLock(null)
    }
}