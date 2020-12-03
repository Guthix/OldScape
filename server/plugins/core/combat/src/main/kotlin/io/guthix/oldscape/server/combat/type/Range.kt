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
import io.guthix.oldscape.server.combat.attackSequence
import io.guthix.oldscape.server.combat.attackSpeed
import io.guthix.oldscape.server.combat.dmg.calcHit
import io.guthix.oldscape.server.combat.dmg.maxRangeHit
import io.guthix.oldscape.server.combat.inCombatWith
import io.guthix.oldscape.server.damage.hit
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.event.NpcAttackedEvent
import io.guthix.oldscape.server.pathing.DestinationRange
import io.guthix.oldscape.server.pathing.breadthFirstSearch
import io.guthix.oldscape.server.task.NormalTask
import io.guthix.oldscape.server.template.ammunitionProjectile
import io.guthix.oldscape.server.template.defenceSequence
import io.guthix.oldscape.server.template.drawBackSpotAnim
import io.guthix.oldscape.server.template.drawBackSpotAnimHeight
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Npc
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.entity.interest.EquipmentType
import kotlin.random.Random

fun Player.rangeAttack(npc: Npc, world: World) {
    val npcDestination = DestinationRange(npc, attackRange, world)
    path = breadthFirstSearch(pos, npcDestination, size, true, world)
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
            equipment[EquipmentType.AMMUNITION.slot] = ammunition.apply { quantity-- }
            animate(attackSequence)
            spotAnimate(ammunition.drawBackSpotAnim, ammunition.drawBackSpotAnimHeight)
            val projectile = world.addProjectile(ammunition.ammunitionProjectile, pos, npc)
            EventBus.schedule(NpcAttackedEvent(npc, player, world))
            world.addTask(NormalTask) { // projectile task
                val damage = calcHit(npc, maxRangeHit()) ?: 0
                val oldNpcPos = npc.pos
                wait(ticks = projectile.lifeTimeServerTicks - 1)
                if (Random.nextDouble(1.0) < 0.8) world.addObject(ammunition.copy(quantity = 1), oldNpcPos)
                npc.animate(npc.defenceSequence)
                if (npc.hit(world, damage)) cancelTasks(NormalTask)
            }
            wait(ticks = attackSpeed)
        }
    }.finalize {
        inCombatWith = null
        turnToLock(null)
    }
}