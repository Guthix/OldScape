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
package io.guthix.oldscape.server.content.skills.magic

import io.guthix.oldscape.cache.config.EnumConfig
import io.guthix.oldscape.server.ServerContext
import io.guthix.oldscape.server.core.combat.CombatSpell
import io.guthix.oldscape.server.core.combat.player.magicAttack
import io.guthix.oldscape.server.core.equipment.CombatProjectileType
import io.guthix.oldscape.server.event.IfOnNpcEvent
import io.guthix.oldscape.server.template.ObjIds
import io.guthix.oldscape.server.template.ProjectileTemplate
import io.guthix.oldscape.server.template.SequenceIds
import io.guthix.oldscape.server.template.SpotAnimIds
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Character
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.entity.SpotAnimation

enum class RegularSpellbookSpell(
    private val obj: Int,
    override val castAnim: Int,
    override val castSound: Int,
    override val castSpotAnim: SpotAnimation,
    override val impactSpotAnim: SpotAnimation,
    override val projectile: ProjectileTemplate,
    override val hit: (World, Player, Character) -> Int
) : CombatSpell {
    WIND_STRIKE(
        obj = ObjIds.NULL_3273,
        castAnim = SequenceIds.SPELL_CAST_711,
        castSound = 220,
        castSpotAnim = SpotAnimation(SpotAnimIds.WIND_STRIKE_CAST_90, height = 92),
        impactSpotAnim = SpotAnimation(SpotAnimIds.WIND_STRIKE_HIT_92, height = 124),
        projectile = CombatProjectileType.MAGIC.createTemplate(91),
        hit = { _, _, _ -> 2 }
    );

    val component: EnumConfig.Component get() = ServerContext.objTemplates[obj].component

    val spellRune1: Int? get() = ServerContext.objTemplates[obj].spellRune1

    val spellRune1Amount: Int? get() = ServerContext.objTemplates[obj].spellRune1Amount

    val spellRune2: Int? get() = ServerContext.objTemplates[obj].spellRune2

    val spellRune2Amount: Int? get() = ServerContext.objTemplates[obj].spellRune2Amount

    val spellRune3: Int? get() = ServerContext.objTemplates[obj].spellRune3

    val spellRune3Amount: Int? get() = ServerContext.objTemplates[obj].spellRune3Amount
}

RegularSpellbookSpell.values().forEach { spell ->
    on(IfOnNpcEvent::class)
        .where { interfaceId == spell.component.interfaceId && interfaceSlotId == spell.component.slot }.then {
            spell.spellRune1?.let { rune ->
                if (player.itemBag.remove(rune, spell.spellRune1Amount!!) == null) {
                    player.senGameMessage(
                        "You do not have enough ${ServerContext.objTemplates[rune].name}s to cast this spell."
                    )
                    return@then
                }
            }
            spell.spellRune2?.let { rune ->
                if (player.itemBag.remove(rune, spell.spellRune2Amount!!) == null) {
                    player.senGameMessage(
                        "You do not have enough ${ServerContext.objTemplates[rune].name}s to cast this spell."
                    )
                    return@then
                }
            }
            spell.spellRune3?.let { rune ->
                if (player.itemBag.remove(rune, spell.spellRune3Amount!!) == null) {
                    player.senGameMessage(
                        "You do not have enough ${ServerContext.objTemplates[rune].name}s to cast this spell."
                    )
                    return@then
                }
            }
            player.magicAttack(npc, world, spell)
        }
}