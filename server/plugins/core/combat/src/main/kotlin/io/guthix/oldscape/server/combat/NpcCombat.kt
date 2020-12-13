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
package io.guthix.oldscape.server.combat

import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.event.PlayerHitEvent
import io.guthix.oldscape.server.pathing.DestinationRectangleDirect
import io.guthix.oldscape.server.pathing.simplePathSearch
import io.guthix.oldscape.server.task.NormalTask
import io.guthix.oldscape.server.template.attackSequence
import io.guthix.oldscape.server.template.attackSpeed
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Npc
import io.guthix.oldscape.server.world.entity.Player

fun startNpcCombat(npc: Npc, player: Player, world: World) {
    var playerDestination = DestinationRectangleDirect(player, world)
    npc.inCombatWith = player
    npc.cancelTasks(NormalTask)
    npc.addTask(NormalTask) { // combat fighting task
        while (true) {
            wait { playerDestination.reached(npc.pos.x, npc.pos.y, npc.size) }
            npc.animate(npc.attackSequence)
            EventBus.schedule(PlayerHitEvent(npc, player, world))
            wait(ticks = npc.attackSpeed)
        }
    }
    npc.addTask(NormalTask) { // following task
        npc.turnToLock(player)
        while (true) {
            playerDestination = DestinationRectangleDirect(player, world)
            npc.path = simplePathSearch(npc.pos, playerDestination, npc.size, world)
            wait(ticks = 1)
            wait { player.lastPos != player.pos }
        }
    }.finalize {
        npc.inCombatWith = null
        npc.turnToLock(null)
    }
}