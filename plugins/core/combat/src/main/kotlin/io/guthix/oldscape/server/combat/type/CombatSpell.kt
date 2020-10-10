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
package io.guthix.oldscape.server.combat.type

import io.guthix.oldscape.cache.config.EnumConfig
import io.guthix.oldscape.server.combat.ProjectileType
import io.guthix.oldscape.server.template.*
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Character
import io.guthix.oldscape.server.world.entity.Player

val ObjTemplate.spellBookKey: Int get() = param[336] as Int

val ObjTemplate.spellRune1: ObjTemplate get() = ObjTemplates[param[365] as Int]

val ObjTemplate.spellRune1Amount: Int get() = param[366] as Int

val ObjTemplate.spellRune2: ObjTemplate get() = ObjTemplates[param[367] as Int]

val ObjTemplate.spellRune2Amount: Int get() = param[368] as Int

val ObjTemplate.spellRune3: ObjTemplate get() = ObjTemplates[param[369] as Int]

val ObjTemplate.spellRune3Amount: Int get() = param[370] as Int

val ObjTemplate.component: EnumConfig.Component get() = EnumConfig.Component.decode(param[596] as Int)

enum class CombatSpell(
    private val obj: ObjTemplate,
    val castAnim: SequenceTemplate,
    val castSound: Int,
    val castSpotAnim: PhysicalSpotAnimTemplate,
    val impactSpotAnim: PhysicalSpotAnimTemplate,
    val projectile: ProjectileTemplate,
    val hit: (World, Player, Character) -> Int
) {
    WIND_STRIKE(
        obj = ObjTemplates.NULL_3273,
        castAnim = SequenceTemplates.SPELL_CAST_711,
        castSound = 220,
        castSpotAnim = SpotAnimTemplates.WIND_STRIKE_CAST_H92_90,
        impactSpotAnim = SpotAnimTemplates.WIND_STRIKE_HIT_H124_92,
        projectile = ProjectileType.MAGIC.createTemplate(91),
        hit = { world, player, target -> 2 }
    );

    val component: EnumConfig.Component get() = obj.component

    val spellRune1: ObjTemplate get() = obj.spellRune1

    val spellRune1Amount: Int get() = obj.spellRune1Amount

    val spellRune2: ObjTemplate get() = obj.spellRune2

    val spellRune2Amount: Int get() = obj.spellRune2Amount

    val spellRune3: ObjTemplate get() = obj.spellRune3

    val spellRune3Amount: Int get() = obj.spellRune3Amount
}