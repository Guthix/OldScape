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

import io.guthix.oldscape.server.combat.dmg.calcHit
import io.guthix.oldscape.server.event.NpcAttackedEvent
import io.guthix.oldscape.server.pathing.DestinationRectangleDirect
import io.guthix.oldscape.server.pathing.simplePathSearch
import io.guthix.oldscape.server.plugin.ConfigDataMissingException
import io.guthix.oldscape.server.task.NormalTask
import io.guthix.oldscape.server.template.attackSpeed
import io.guthix.oldscape.server.template.sequences
import io.guthix.oldscape.server.world.entity.HitMark

on(NpcAttackedEvent::class).then {
    if (npc.inCombatWith == player) return@then
    var playerDestination = DestinationRectangleDirect(player, world.map)
    npc.inCombatWith = player
    npc.cancelTasks(NormalTask)
    npc.addTask(NormalTask) { // combat fighting task
        while (true) {
            wait { playerDestination.reached(npc.pos.x, npc.pos.y, npc.size) }
            npc.animate(npc.sequences?.attack ?: throw ConfigDataMissingException(
                "NPC $npc has no attack sequence."
            ))
            val damage = npc.calcHit(player) ?: 0
            val hmColor = if (damage == 0) HitMark.Color.BLUE else HitMark.Color.RED
            player.hit(hmColor, damage, 0)
            player.animate(player.defenceSequence!!)
            wait(ticks = npc.attackSpeed!!)
        }
    }
    npc.addTask(NormalTask) { // following task
        npc.turnToLock(player)
        while (true) {
            playerDestination = DestinationRectangleDirect(player, world.map)
            npc.path = simplePathSearch(npc.pos, playerDestination, npc.size, world.map)
            wait(ticks = 1)
            wait { player.lastPos != player.pos }
        }
    }.onCancel {
        npc.inCombatWith = null
        npc.turnToLock(null)
    }
}