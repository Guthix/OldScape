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

import io.guthix.oldscape.server.blueprints.AttackType
import io.guthix.oldscape.server.combat.dmg.calcHit
import io.guthix.oldscape.server.combat.dmg.maxMeleeHit
import io.guthix.oldscape.server.combat.dmg.maxRangeHit
import io.guthix.oldscape.server.dimensions.TileUnit
import io.guthix.oldscape.server.dimensions.tiles
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.event.NpcAttackedEvent
import io.guthix.oldscape.server.event.NpcClickEvent
import io.guthix.oldscape.server.pathing.DestinationRange
import io.guthix.oldscape.server.pathing.DestinationRectangleDirect
import io.guthix.oldscape.server.pathing.breadthFirstSearch
import io.guthix.oldscape.server.task.NormalTask
import io.guthix.oldscape.server.world.entity.HitMark
import io.guthix.oldscape.server.world.entity.Sequence
import kotlin.math.floor

on(NpcClickEvent::class).where { contextMenuEntry == "Attack" }.then {
    if (player.inCombatWith == npc) return@then
    player.turnToLock(npc)
    when (player.currentStyle.attackType) {
        AttackType.RANGED -> rangeAttack(range = player.equipment.weapon?.attackRange ?: 1.tiles)
        else -> meleeAttack()
    }
}

fun NpcClickEvent.meleeAttack() {
    val npcDestination = DestinationRectangleDirect(npc, world.map)
    player.path = breadthFirstSearch(player.pos, npcDestination, player.size, true, world.map)
    player.inCombatWith = npc
    player.cancelTasks(NormalTask)
    player.addTask(NormalTask) {
        wait { npcDestination.reached(player.pos.x, player.pos.y, player.size) }
        EventBus.schedule(NpcAttackedEvent(npc, player, world))
        while (true) { // start player combat
            player.animate(Sequence(id = player.attackSequence))
            val damage = player.calcHit(npc, player.maxMeleeHit()) ?: 0
            val hmColor = if (damage == 0) HitMark.Color.BLUE else HitMark.Color.RED
            npc.hit(hmColor, damage, 0)
            npc.animate(Sequence(id = npc.combatSequences?.defence ?: -1))
            wait(ticks = player.equipment.weapon?.attackSpeed ?: 5)
        }
    }.onCancel {
        player.inCombatWith = null
        player.turnToLock(null)
    }
}

fun NpcClickEvent.rangeAttack(range: TileUnit) {
    val npcDestination = DestinationRange(npc, range, world.map)
    player.path = breadthFirstSearch(player.pos, npcDestination, player.size, true, world.map)
    player.inCombatWith = npc
    player.cancelTasks(NormalTask)
    player.addTask(NormalTask) {
        wait { npcDestination.reached(player.pos.x, player.pos.y, player.size) }
        while (true) { // start player combat
            player.animate(Sequence(id = player.attackSequence))
            world.map.addProjectile(Arrow(10, player.pos, npc))
            world.addTask(NormalTask) { // projectile task
                val npcPos = npc.pos
                wait(ticks = 1 + floor((3.0 + player.pos.distanceTo(npcPos)) / 6.0).toInt())
                val damage = player.calcHit(npc, player.maxRangeHit()) ?: 0
                val hmColor = if (damage == 0) HitMark.Color.BLUE else HitMark.Color.RED
                npc.hit(hmColor, damage, 0)
                EventBus.schedule(NpcAttackedEvent(npc, player, world))
                // TODO drop ammunition
            }

            npc.animate(Sequence(id = npc.combatSequences?.defence ?: -1))
            wait(ticks = player.equipment.weapon?.attackSpeed ?: 5)
        }
    }.onCancel {
        player.inCombatWith = null
        player.turnToLock(null)
    }
}