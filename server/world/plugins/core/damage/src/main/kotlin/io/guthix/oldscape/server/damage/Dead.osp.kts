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
package io.guthix.oldscape.server.damage

import io.guthix.oldscape.server.event.NpcDiedEvent
import io.guthix.oldscape.server.event.PlayerDiedEvent
import io.guthix.oldscape.server.task.NormalTask

on(NpcDiedEvent::class).then {
    val npc = world.removeNpc(npc)
    npc.health = npc.stats.health
    npc.pos = npc.spawnPos.copy()
    world.addTask(NormalTask) {
        wait(ticks = 5)
        world.addNpc(npc)
    }
}

on(PlayerDiedEvent::class).then {
    player.health = 99
    player.teleport(player.spawnPos)
}