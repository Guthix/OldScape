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

import io.guthix.oldscape.server.core.combat.dmg.maxRangeHit
import io.guthix.oldscape.server.core.combat.event.NpcHitByPlayerEvent
import io.guthix.oldscape.server.core.combat.inCombatWith
import io.guthix.oldscape.server.core.equipment.template.ammunitionProjectile
import io.guthix.oldscape.server.core.equipment.template.drawBackSpotAnim
import io.guthix.oldscape.server.core.equipment.template.drawBackSpotAnimHeight
import io.guthix.oldscape.server.core.pathing.DestinationRange
import io.guthix.oldscape.server.core.pathing.breadthFirstSearch
import io.guthix.oldscape.server.core.pathing.simplePathSearch
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.task.NormalTask
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Npc
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.entity.interest.EquipmentType
import kotlin.random.Random

fun Player.startRangeAttack(npc: Npc, world: World) {
    cancelTasks(NormalTask)
    var npcDestination = DestinationRange(npc, attackRange, world)
    path = breadthFirstSearch(pos, npcDestination, size, true, world)
    addTask(NormalTask) {
        inCombatWith = npc
        main@ while (true) { // start player combat
            wait { npcDestination.reached(pos.x, pos.y, size) }
            val ammunition = equipment.ammunition
            if (ammunition == null || ammunition.quantity <= 0) {
                sendGameMessage("There is no ammo left in your quiver.")
                cancel()
                break@main
            }
            equipment[EquipmentType.AMMUNITION.slot] = ammunition.apply { quantity-- }
            animate(attackSequence)
            spotAnimate(ammunition.drawBackSpotAnim, ammunition.drawBackSpotAnimHeight)
            val projectile = world.addProjectile(ammunition.ammunitionProjectile, pos, npc)
            world.addTask(NormalTask) { // projectile task
                val oldNpcPos = npc.pos
                wait(ticks = projectile.lifeTimeServerTicks - 1)
                if (Random.nextDouble(1.0) < 0.8) world.addObject(ammunition.copy(quantity = 1), oldNpcPos)
                EventBus.schedule(NpcHitByPlayerEvent(this@startRangeAttack, npc, world, maxRangeHit()))
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