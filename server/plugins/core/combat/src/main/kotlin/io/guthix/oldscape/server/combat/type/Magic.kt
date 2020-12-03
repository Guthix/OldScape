/*
 * Copyright 2018-2020 Guthix
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.guthix.oldscape.server.combat.type

import io.guthix.oldscape.server.combat.attackRange
import io.guthix.oldscape.server.combat.attackSpeed
import io.guthix.oldscape.server.combat.dmg.calcHit
import io.guthix.oldscape.server.combat.inCombatWith
import io.guthix.oldscape.server.damage.hit
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.event.NpcAttackedEvent
import io.guthix.oldscape.server.pathing.DestinationRange
import io.guthix.oldscape.server.pathing.breadthFirstSearch
import io.guthix.oldscape.server.task.NormalTask
import io.guthix.oldscape.server.template.SpotAnimIds
import io.guthix.oldscape.server.template.defenceSequence
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Npc
import io.guthix.oldscape.server.world.entity.Player

fun Player.magicAttack(
    npc: Npc,
    world: World,
    spellTemplate: CombatSpell
) {
    val npcDestination = DestinationRange(npc, attackRange, world)
    path = breadthFirstSearch(pos, npcDestination, size, true, world)
    inCombatWith = npc
    cancelTasks(NormalTask)
    val player = this
    addTask(NormalTask) combatTask@{
        main@ while (true) { // start player combat
            wait { npcDestination.reached(pos.x, pos.y, size) }
            animate(spellTemplate.castAnim)
            spotAnimate(spellTemplate.castSpotAnim, spellTemplate.castSpotAnimHeight)
            // TODO sound
            val projectile = world.addProjectile(spellTemplate.projectile, player.pos, npc)
            EventBus.schedule(NpcAttackedEvent(npc, player, world))
            world.addTask(NormalTask) {
                val damage = calcHit(npc, spellTemplate.hit(world, player, npc))
                if (damage == null) {
                    npc.spotAnimate(SpotAnimIds.SPLASH_85, height = 123, projectile.lifetimeClientTicks) // sound 227
                    // TODO sound
                } else {
                    wait(ticks = projectile.lifeTimeServerTicks - 1)
                    npc.animate(npc.defenceSequence)
                    npc.spotAnimate(spellTemplate.impactSpotAnim, spellTemplate.impactSpotAnimHeight)
                    // TODO sound
                    if (npc.hit(world, damage)) cancelTasks(NormalTask)
                }
            }
            wait(ticks = attackSpeed)
        }
    }.finalize {
        inCombatWith = null
        turnToLock(null)
    }
}