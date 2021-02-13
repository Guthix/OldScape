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

import io.guthix.oldscape.server.ServerContext
import io.guthix.oldscape.server.core.combat.CombatSpell
import io.guthix.oldscape.server.core.combat.dmg.calcHit
import io.guthix.oldscape.server.core.combat.event.PlayerHitByNpcEvent
import io.guthix.oldscape.server.core.combat.inCombatWith
import io.guthix.oldscape.server.core.damage.hit
import io.guthix.oldscape.server.event.IfOnNpcEvent
import io.guthix.oldscape.server.event.NpcClickEvent
import io.guthix.oldscape.server.task.NormalTask

on(NpcClickEvent::class).where { contextMenuEntry == "Attack" }.then {
    if (player.inCombatWith != npc) player.attackNpc(npc, world)
}

on(PlayerHitByNpcEvent::class).then {
    if (player.inCombatWith == null && player.autoRetaliate) player.attackNpc(npc, world)
    val damage = npc.calcHit(player)
    if (damage == null) {
        if (spotAnimOnFail == null) {
            if (player.hit(world, 0)) {
                npc.cancelTasks(NormalTask)
            } else {
                player.animate(player.defenceSequence)
            }
        } else {
            player.spotAnimate(spotAnimOnFail)
        }
    } else {
        player.animate(player.defenceSequence)
        if (player.hit(world, damage)) {
            npc.cancelTasks(NormalTask)
        } else {
            player.animate(player.defenceSequence)
        }
        spotAnimOnSuccess?.let(player::spotAnimate)
    }
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

