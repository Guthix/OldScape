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
package io.guthix.oldscape.server.combat.npc

import io.guthix.oldscape.server.combat.dmg.calcHit
import io.guthix.oldscape.server.combat.inCombatWith
import io.guthix.oldscape.server.damage.hit
import io.guthix.oldscape.server.event.NpcHitByPlayerEvent
import io.guthix.oldscape.server.task.NormalTask
import io.guthix.oldscape.server.template.defenceSequence

on(NpcHitByPlayerEvent::class).then {
    if(npc.inCombatWith == null) npc.attackPlayer(player, world)
    val damage = player.calcHit(npc, maxHit)
    if(damage == null) {
        if(spotAnimOnFail == null) {
            if(npc.hit(world, 0)) {
                player.cancelTasks(NormalTask)
            } else {
                npc.animate(npc.defenceSequence)
            }
        } else {
            npc.spotAnimate(spotAnimOnFail)
        }
    } else {
        if(npc.hit(world, damage)) {
            player.cancelTasks(NormalTask)
        } else {
            npc.animate(npc.defenceSequence)
        }
        spotAnimOnSuccess?.let(npc::spotAnimate)
    }
}