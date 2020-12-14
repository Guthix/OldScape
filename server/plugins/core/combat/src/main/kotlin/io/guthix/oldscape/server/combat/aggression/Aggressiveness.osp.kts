
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
package io.guthix.oldscape.server.combat.aggression

import io.guthix.oldscape.server.combat.attackPlayer
import io.guthix.oldscape.server.combat.inCombatWith
import io.guthix.oldscape.server.event.NpcSpawnedEvent
import io.guthix.oldscape.server.task.NormalTask
import io.guthix.oldscape.server.template.AggresiveType
import io.guthix.oldscape.server.template.aggressiveType

on(NpcSpawnedEvent::class).then {
    when(val aggressiveness = npc.aggressiveType) {
        null -> return@then
        AggresiveType.Never -> return@then
        is AggresiveType.Combat -> npc.addTask(AggressionTask) {
            val npcLevel = npc.combatLevel ?: 0
            while(true) {
                world.findPlayers(npc.pos, aggressiveness.range).forEach {
                    if(it.pos.withInDistanceOf(npc.pos, aggressiveness.range) && it.combatLevel <= npcLevel * 2) {
                        npc.addTask(NormalTask) { npc.attackPlayer(it, world) }
                    }
                }
                wait(ticks = 1)
                wait { npc.inCombatWith == null }
            }
        }
        is AggresiveType.Always -> npc.addTask(AggressionTask) {
            while(true) {
                world.findPlayers(npc.pos, aggressiveness.range).forEach {
                    if(it.pos.withInDistanceOf(npc.pos, aggressiveness.range)) {
                        npc.addTask(NormalTask) { npc.attackPlayer(it, world) }
                    }
                }
                wait(ticks = 1)
                wait { npc.inCombatWith == null }
            }
        }
    }
}