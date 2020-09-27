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

import io.guthix.oldscape.server.combat.ProjectileType
import io.guthix.oldscape.server.template.*
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Character
import io.guthix.oldscape.server.world.entity.Player

enum class CombatSpell(
    val interfaceId: Int,
    val interfaceSlotId: Int,
    val castAnim: SequenceTemplate,
    val castSound: Int,
    val castSpotAnim: PhysicalSpotAnimTemplate,
    val impactSpotAnim: PhysicalSpotAnimTemplate,
    val projectile: ProjectileTemplate,
    val hit: (World, Player, Character) -> Int
) {
    WIND_STRIKE(
        interfaceId = 198,
        interfaceSlotId = 10,
        castAnim = SequenceTemplates.SPELL_CAST_711,
        castSound = 220,
        castSpotAnim = SpotAnimTemplates.WIND_STRIKE_CAST_H124_90,
        impactSpotAnim = SpotAnimTemplates.WIND_STRIKE_HIT_H92_92,
        projectile = ProjectileType.MAGIC.createTemplate(91),
        hit = { world, player, target -> 2 }
    )
}