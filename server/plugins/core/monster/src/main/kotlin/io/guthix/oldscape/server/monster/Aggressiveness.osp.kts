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
package io.guthix.oldscape.server.monster

import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.event.NpcAttackEvent
import io.guthix.oldscape.server.event.PlayerMovedEvent
import io.guthix.oldscape.server.template.AggresiveType
import io.guthix.oldscape.server.template.aggressiveType
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Npc
import io.guthix.oldscape.server.world.entity.Player

on(PlayerMovedEvent::class).then {
    player.localNpcs.forEach { npc ->
        checkNpcAggressive(player, npc, world)
    }
}

fun checkNpcAggressive(player: Player, npc: Npc, world: World) {
    when(val aggressiveness = npc.aggressiveType) {
        AggresiveType.Never -> return
        is AggresiveType.Combat -> {
            val npcLevel = npc.combatLevel ?: 0
            if(player.pos.withInDistanceOf(npc.pos, aggressiveness.range) && player.combatLevel <= npcLevel * 2) {
                EventBus.schedule(NpcAttackEvent(npc, player, world))
            }
        }
        is AggresiveType.Always -> {
            if(player.pos.withInDistanceOf(npc.pos, aggressiveness.range)) {
                EventBus.schedule(NpcAttackEvent(npc, player, world))
            }
        }
    }
}