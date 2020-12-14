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

import io.guthix.oldscape.server.ServerContext
import io.guthix.oldscape.server.combat.dmg.calcHit
import io.guthix.oldscape.server.damage.hit
import io.guthix.oldscape.server.event.IfOnNpcEvent
import io.guthix.oldscape.server.event.NpcClickEvent
import io.guthix.oldscape.server.event.PlayerHitEvent

on(NpcClickEvent::class).where { contextMenuEntry == "Attack" }.then {
    player.attackNpc(npc, world)
}

on(PlayerHitEvent::class).then {
    val damage = npc.calcHit(player) ?: 0
    player.animate(player.defenceSequence)
    player.hit(world, damage)
}

CombatSpell.values().forEach { spell ->
    on(IfOnNpcEvent::class)
        .where { interfaceId == spell.component.interfaceId && interfaceSlotId == spell.component.slot }.then {
            if (player.itemBag.remove(spell.spellRune1, spell.spellRune1Amount) == null) {
                player.senGameMessage(
                    "You do not have enough ${ServerContext.objTemplates[spell.spellRune1].name}s to cast this spell."
                )
                return@then
            }
            if (player.itemBag.remove(spell.spellRune2, spell.spellRune2Amount) == null) {
                player.senGameMessage(
                    "You do not have enough ${ServerContext.objTemplates[spell.spellRune2].name}s to cast this spell."
                )
                return@then
            }
            if (player.itemBag.remove(spell.spellRune3, spell.spellRune3Amount) == null) {
                player.senGameMessage(
                    "You do not have enough ${ServerContext.objTemplates[spell.spellRune3].name}s to cast this spell."
                )
                return@then
            }
            player.magicAttack(npc, world, spell)
        }
}

