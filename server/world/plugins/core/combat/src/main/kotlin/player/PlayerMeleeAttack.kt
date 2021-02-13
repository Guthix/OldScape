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

import io.guthix.oldscape.server.core.combat.dmg.maxMeleeHit
import io.guthix.oldscape.server.core.combat.event.NpcHitByPlayerEvent
import io.guthix.oldscape.server.core.combat.inCombatWith
import io.guthix.oldscape.server.core.pathing.DestinationRectangleDirect
import io.guthix.oldscape.server.core.pathing.breadthFirstSearch
import io.guthix.oldscape.server.core.pathing.simplePathSearch
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.task.NormalTask
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Npc
import io.guthix.oldscape.server.world.entity.Player

fun Player.startMeleeAttack(npc: Npc, world: World) {
    cancelTasks(NormalTask)
    var npcDestination = DestinationRectangleDirect(npc, world)
    path = breadthFirstSearch(pos, npcDestination, size, findAlternative = true, world)
    addTask(NormalTask) {
        inCombatWith = npc
        while (true) { // start player combat
            wait { npcDestination.reached(pos.x, pos.y, size) }
            animate(attackSequence)
            EventBus.schedule(NpcHitByPlayerEvent(this@startMeleeAttack, npc, world, maxMeleeHit()))
            wait(ticks = attackSpeed)
        }
    }.finalize {
        inCombatWith = null
    }
    addTask(NormalTask) {
        inCombatWith = npc
        turnToLock(npc)
        wait { npcDestination.reached(pos.x, pos.y, size) }
        while (true) {
            wait { npc.lastPos != npc.pos }
            npcDestination = DestinationRectangleDirect(npc, world)
            path = simplePathSearch(pos, npcDestination, size, world)
            wait(ticks = 1)
        }
    }.finalize {
        turnToLock(null)
    }
}