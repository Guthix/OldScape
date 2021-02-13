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
package io.guthix.oldscape.server.core.combat.npc

import io.guthix.oldscape.server.core.combat.event.PlayerHitByNpcEvent
import io.guthix.oldscape.server.core.combat.inCombatWith
import io.guthix.oldscape.server.core.monster.template.attackSequence
import io.guthix.oldscape.server.core.monster.template.attackSpeed
import io.guthix.oldscape.server.core.monster.template.attackType
import io.guthix.oldscape.server.core.pathing.DestinationRectangleDirect
import io.guthix.oldscape.server.core.pathing.simplePathSearch
import io.guthix.oldscape.server.core.stat.AttackType
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.task.NormalTask
import io.guthix.oldscape.server.task.TaskType
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Npc
import io.guthix.oldscape.server.world.entity.Player

object AggressionTask : TaskType

fun Npc.attackPlayer(player: Player, world: World): Unit = when (attackType) {
    AttackType.RANGED -> TODO()
    AttackType.MAGIC -> TODO()
    else -> meleeAttack(player, world)
}

internal fun Npc.meleeAttack(player: Player, world: World) {
    cancelTasks(NormalTask)
    var playerDestination = DestinationRectangleDirect(player, world)
    path = simplePathSearch(pos, playerDestination, size, world)
    addTask(NormalTask) { // combat fighting task
        inCombatWith = player
        while (true) {
            wait { playerDestination.reached(pos.x, pos.y, size) }
            animate(attackSequence)
            EventBus.schedule(PlayerHitByNpcEvent(this@meleeAttack, player, world))
            wait(ticks = attackSpeed)
        }
    }.finalize {
        inCombatWith = null
    }
    addTask(NormalTask) { // following task
        turnToLock(player)
        wait { playerDestination.reached(pos.x, pos.y, size) }
        while (true) {
            wait { player.lastPos != player.pos }
            playerDestination = DestinationRectangleDirect(player, world)
            path = simplePathSearch(pos, playerDestination, size, world)
            wait(ticks = 1)
        }
    }.finalize {
        turnToLock(null)
    }
}