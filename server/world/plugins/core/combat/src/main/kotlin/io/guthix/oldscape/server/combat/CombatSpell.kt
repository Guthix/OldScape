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
package io.guthix.oldscape.server.combat

import io.guthix.oldscape.cache.config.EnumConfig
import io.guthix.oldscape.server.ServerContext
import io.guthix.oldscape.server.equipment.CombatProjectileType
import io.guthix.oldscape.server.template.*
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Character
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.entity.SpotAnimation

val ObjTemplate.spellBookKey: Int get() = param[336] as Int

val ObjTemplate.spellRune1: Int get() = param[365] as Int

val ObjTemplate.spellRune1Amount: Int get() = param[366] as Int

val ObjTemplate.spellRune2: Int get() = param[367] as Int

val ObjTemplate.spellRune2Amount: Int get() = param[368] as Int

val ObjTemplate.spellRune3: Int get() = param[369] as Int

val ObjTemplate.spellRune3Amount: Int get() = param[370] as Int

val ObjTemplate.component: EnumConfig.Component get() = EnumConfig.Component.decode(param[596] as Int)

enum class CombatSpell(
    private val obj: Int,
    val castAnim: Int,
    val castSound: Int,
    val castSpotAnim: SpotAnimation,
    val impactSpotAnim: SpotAnimation,
    val projectile: ProjectileTemplate,
    val hit: (World, Player, Character) -> Int
) {
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

    val spellRune1: Int get() = ServerContext.objTemplates[obj].spellRune1

    val spellRune1Amount: Int get() = ServerContext.objTemplates[obj].spellRune1Amount

    val spellRune2: Int get() = ServerContext.objTemplates[obj].spellRune2

    val spellRune2Amount: Int get() = ServerContext.objTemplates[obj].spellRune2Amount

    val spellRune3: Int get() = ServerContext.objTemplates[obj].spellRune3

    val spellRune3Amount: Int get() = ServerContext.objTemplates[obj].spellRune3Amount
}