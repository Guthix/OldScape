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

import io.guthix.oldscape.server.blueprints.SequenceBlueprint
import io.guthix.oldscape.server.blueprints.SpotAnimBlueprint
import io.guthix.oldscape.server.combat.attackRange
import io.guthix.oldscape.server.combat.attackSpeed
import io.guthix.oldscape.server.combat.combatSequences
import io.guthix.oldscape.server.combat.dmg.calcHit
import io.guthix.oldscape.server.combat.inCombatWith
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.event.NpcAttackedEvent
import io.guthix.oldscape.server.pathing.DestinationRange
import io.guthix.oldscape.server.pathing.breadthFirstSearch
import io.guthix.oldscape.server.plugin.ConfigDataMissingException
import io.guthix.oldscape.server.task.NormalTask
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.HitMark
import io.guthix.oldscape.server.world.entity.Npc
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.entity.Projectile

val SPLASH_ANIMATION_BLUEPRINT: SpotAnimBlueprint = SpotAnimBlueprint(id = 85, height = 124) // sound 227

fun Player.magicAttack(
    npc: Npc,
    world: World,
    castAnim: SequenceBlueprint,
    spellAnim: SpotAnimBlueprint,
    projectile: Projectile,
    maxHit: (Player, Npc) -> Int
) {
    val npcDestination = DestinationRange(npc, attackRange, world.map)
    path = breadthFirstSearch(pos, npcDestination, size, true, world.map)
    inCombatWith = npc
    cancelTasks(NormalTask)
    val player = this
    addTask(NormalTask) {
        main@ while (true) { // start player combat
            wait { npcDestination.reached(pos.x, pos.y, size) }
            animate(castAnim)
            spotAnimate(spellAnim)
            val projectileTimeInTicks = projectile.lifetime / 30 - 1
            world.map.addProjectile(projectile)
            EventBus.schedule(NpcAttackedEvent(npc, player, world))
            world.addTask(NormalTask) {
                val damage = calcHit(npc, maxHit(player, npc))
                wait(ticks = projectileTimeInTicks)
                if (damage != null) {
                    val hmColor = if (damage == 0) HitMark.Color.BLUE else HitMark.Color.RED
                    npc.hit(hmColor, damage, 0)
                    npc.animate(npc.combatSequences?.defence ?: throw ConfigDataMissingException(
                        "No block animation for npc $npc."
                    ))
                } else {
                    npc.spotAnimate(SPLASH_ANIMATION_BLUEPRINT) // TODO splash animation delay in client
                }
            }
            wait(ticks = attackSpeed)
        }
    }.onCancel {
        inCombatWith = null
        turnToLock(null)
    }
}