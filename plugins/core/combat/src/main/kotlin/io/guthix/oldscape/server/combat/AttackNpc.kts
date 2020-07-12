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
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.combat

import io.guthix.oldscape.server.blueprints.AttackType
import io.guthix.oldscape.server.combat.dmg.calcHit
import io.guthix.oldscape.server.combat.dmg.maxMeleeHit
import io.guthix.oldscape.server.combat.dmg.maxRangeHit
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.event.NpcAttackedEvent
import io.guthix.oldscape.server.event.NpcClickEvent
import io.guthix.oldscape.server.pathing.DestinationRange
import io.guthix.oldscape.server.pathing.DestinationRectangleDirect
import io.guthix.oldscape.server.pathing.breadthFirstSearch
import io.guthix.oldscape.server.plugin.ConfigDataMissingException
import io.guthix.oldscape.server.task.NormalTask
import io.guthix.oldscape.server.world.entity.AmmunitionEquipment
import io.guthix.oldscape.server.world.entity.HitMark
import io.guthix.oldscape.server.world.entity.Sequence
import kotlin.math.floor
import kotlin.random.Random

on(NpcClickEvent::class).where { contextMenuEntry == "Attack" }.then {
    if (player.inCombatWith == npc) return@then
    player.turnToLock(npc)
    when (player.currentStyle.attackType) {
        AttackType.RANGED -> rangeAttack()
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
            wait(ticks = player.attackSpeed)
        }
    }.onCancel {
        player.inCombatWith = null
        player.turnToLock(null)
    }
}

fun NpcClickEvent.rangeAttack() {
    val npcDestination = DestinationRange(npc, player.attackRange, world.map)
    player.path = breadthFirstSearch(player.pos, npcDestination, player.size, true, world.map)
    player.inCombatWith = npc
    player.cancelTasks(NormalTask)
    player.addTask(NormalTask) {
        main@ while (true) { // start player combat
            wait { npcDestination.reached(player.pos.x, player.pos.y, player.size) }
            val ammunition = player.equipment.ammunition
            if(ammunition == null || ammunition.quantity <= 0) {
                player.senGameMessage("There is no ammo left in your quiver.")
                cancel()
                break@main
            }
            player.animate(Sequence(id = player.attackSequence))
            ammunition.drawBackSpotAnim?.let(player::spotAnimate)
            world.map.addProjectile(ammunition.createProjectile(player.pos, npc))
            player.topInterface.equipment[AmmunitionEquipment.slot] = ammunition.apply { quantity-- }
            val shootPos = npc.pos
            world.addTask(NormalTask) { // projectile task
                val npcPos = npc.pos
                wait(ticks = 1 + floor((3.0 + player.pos.distanceTo(npcPos)) / 6.0).toInt())
                val damage = player.calcHit(npc, player.maxRangeHit()) ?: 0
                val hmColor = if (damage == 0) HitMark.Color.BLUE else HitMark.Color.RED
                npc.hit(hmColor, damage, 0)
                if(Random.nextDouble(1.0) < 0.8) world.map.addObject(shootPos, AmmunitionEquipment(ammunition.id, 1))
                EventBus.schedule(NpcAttackedEvent(npc, player, world))
            }
            npc.animate(Sequence(id = npc.combatSequences?.defence ?: -1))
            wait(ticks = player.attackSpeed)
        }
    }.onCancel {
        player.inCombatWith = null
        player.turnToLock(null)
    }
}