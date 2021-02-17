/*
 * Copyright 2018-2021 Guthix
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
package io.guthix.oldscape.server.core.combat.player

import io.guthix.oldscape.server.core.combat.CombatSpell
import io.guthix.oldscape.server.core.combat.dmg.maxMagicHit
import io.guthix.oldscape.server.core.combat.event.NpcHitByPlayerEvent
import io.guthix.oldscape.server.core.combat.inCombatWith
import io.guthix.oldscape.server.core.pathing.DestinationRange
import io.guthix.oldscape.server.core.pathing.breadthFirstSearch
import io.guthix.oldscape.server.core.pathing.simplePathSearch
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.task.NormalTask
import io.guthix.oldscape.cache.SpotAnimIds
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Npc
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.entity.SpotAnimation

val spashAnimation: SpotAnimation = SpotAnimation(SpotAnimIds.SPLASH_85, height = 123)

fun Player.startMagicAttack(
    npc: Npc,
    world: World,
    spellTemplate: CombatSpell
) {
    cancelTasks(NormalTask)
    var npcDestination = DestinationRange(npc, attackRange, world)
    path = breadthFirstSearch(pos, npcDestination, size, true, world)
    addTask(NormalTask) {
        inCombatWith = npc
        while (true) { // start player combat
            wait { npcDestination.reached(pos.x, pos.y, size) }
            animate(spellTemplate.castAnim)
            spotAnimate(spellTemplate.castSpotAnim)
            val projectile = world.addProjectile(spellTemplate.projectile, pos, npc)
            world.addTask(NormalTask) {
                wait(ticks = projectile.lifeTimeServerTicks - 1)
                EventBus.schedule(
                    NpcHitByPlayerEvent(
                        this@startMagicAttack,
                        npc,
                        world,
                        maxMagicHit(spellTemplate.hit(world, this@startMagicAttack, npc)),
                        spotAnimOnSuccess = spellTemplate.impactSpotAnim,
                        spotAnimOnFail = spashAnimation
                    )
                )
            }
            wait(ticks = attackSpeed)
        }
    }.finalize {
        inCombatWith = null
    }
    addTask(NormalTask) {
        turnToLock(npc)
        wait { npcDestination.reached(pos.x, pos.y, size) }
        while (true) {
            wait { npc.lastPos != npc.pos }
            npcDestination = DestinationRange(npc, attackRange, world)
            path = simplePathSearch(pos, npcDestination, size, world)
            wait(ticks = 1)
        }
    }.finalize {
        turnToLock(null)
    }
}

fun Player.magicAttack(
    npc: Npc,
    world: World,
    spellTemplate: CombatSpell
) {
    cancelTasks(NormalTask)
    val npcDestination = DestinationRange(npc, attackRange, world)
    path = breadthFirstSearch(pos, npcDestination, size, true, world)
    addTask(NormalTask) {
        wait { npcDestination.reached(pos.x, pos.y, size) }
        animate(spellTemplate.castAnim)
        spotAnimate(spellTemplate.castSpotAnim)
        val projectile = world.addProjectile(spellTemplate.projectile, pos, npc)
        world.addTask(NormalTask) {
            wait(ticks = projectile.lifeTimeServerTicks - 1)
            EventBus.schedule(
                NpcHitByPlayerEvent(
                    this@magicAttack,
                    npc,
                    world,
                    maxMagicHit(spellTemplate.hit(world, this@magicAttack, npc)),
                    spotAnimOnSuccess = spellTemplate.impactSpotAnim,
                    spotAnimOnFail = spashAnimation
                )
            )
        }
        wait(ticks = attackSpeed)
    }.finalize {
        inCombatWith = null
    }
    addTask(NormalTask) {
        wait { npcDestination.reached(pos.x, pos.y, size) }
        turnTo(npc)
    }
}

