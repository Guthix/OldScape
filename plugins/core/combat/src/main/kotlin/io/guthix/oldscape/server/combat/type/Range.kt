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

import io.guthix.oldscape.server.combat.*
import io.guthix.oldscape.server.combat.dmg.calcHit
import io.guthix.oldscape.server.combat.dmg.maxRangeHit
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.event.NpcAttackedEvent
import io.guthix.oldscape.server.pathing.DestinationRange
import io.guthix.oldscape.server.pathing.breadthFirstSearch
import io.guthix.oldscape.server.plugin.ConfigDataMissingException
import io.guthix.oldscape.server.task.NormalTask
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.AmmunitionEquipment
import io.guthix.oldscape.server.world.entity.HitMark
import io.guthix.oldscape.server.world.entity.Npc
import io.guthix.oldscape.server.world.entity.Player
import kotlin.random.Random

fun Player.rangeAttack(npc: Npc, world: World) {
    val npcDestination = DestinationRange(npc, attackRange, world.map)
    path = breadthFirstSearch(pos, npcDestination, size, true, world.map)
    inCombatWith = npc
    cancelTasks(NormalTask)
    val player = this
    addTask(NormalTask) {
        main@ while (true) { // start player combat
            wait { npcDestination.reached(pos.x, pos.y, size) }
            val ammunition = equipment.ammunition
            if (ammunition == null || ammunition.quantity <= 0) {
                senGameMessage("There is no ammo left in your quiver.")
                cancel()
                break@main
            }
            topInterface.equipment[AmmunitionEquipment.slot] = ammunition.apply { quantity-- }
            animate(attackSequence)
            ammunition.drawBackSpotAnim?.let(::spotAnimate)
            val projectile = ammunition.createProjectile(pos, npc)
            world.map.addProjectile(ammunition.createProjectile(pos, npc))
            EventBus.schedule(NpcAttackedEvent(npc, player, world))
            world.addTask(NormalTask) { // projectile task
                val damage = calcHit(npc, maxRangeHit()) ?: 0
                val oldNpcPos = npc.pos
                wait(ticks = projectile.lifetime / 30 - 1)
                npc.animate(npc.combatSequences?.defence ?: throw ConfigDataMissingException(
                    "No block animation for npc $npc."
                ))
                val hmColor = if (damage == 0) HitMark.Color.BLUE else HitMark.Color.RED
                npc.hit(hmColor, damage, 0)
                if (Random.nextDouble(1.0) < 0.8) world.map.addObject(oldNpcPos, AmmunitionEquipment(ammunition.id, 1))
            }
            wait(ticks = attackSpeed)
        }
    }.onCancel {
        inCombatWith = null
        turnToLock(null)
    }
}