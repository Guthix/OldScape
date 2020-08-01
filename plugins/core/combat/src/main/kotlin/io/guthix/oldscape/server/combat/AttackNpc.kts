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

import io.guthix.oldscape.server.combat.type.meleeAttack
import io.guthix.oldscape.server.combat.type.rangeAttack
import io.guthix.oldscape.server.config.AttackType
import io.guthix.oldscape.server.event.NpcClickEvent

on(NpcClickEvent::class).where { contextMenuEntry == "Attack" }.then {
    if (player.inCombatWith == npc) return@then
    player.turnToLock(npc)
    when (player.currentStyle.attackType) {
        AttackType.RANGED -> player.rangeAttack(npc, world)
        else -> player.meleeAttack(npc, world)
    }
}