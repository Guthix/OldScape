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

import io.guthix.oldscape.server.Property
import io.guthix.oldscape.server.stat.AttackType
import io.guthix.oldscape.server.stat.StatMultiplier
import io.guthix.oldscape.server.stat.StyleBonus
import io.guthix.oldscape.server.template.ProjectileTemplate
import io.guthix.oldscape.server.world.entity.Character

enum class CombatProjectileType(
    val startHeight: Int,
    val endHeight: Int,
    val speed: Int,
    val speedDelay: Int,
    val delay: Int,
    val angle: Int,
    val steepness: Int
) {
    ARROW(startHeight = 40, endHeight = 36, speed = 5, speedDelay = 5, delay = 41, angle = 15, steepness = 11),
    BOLT(startHeight = 38, endHeight = 36, speed = 5, speedDelay = 5, delay = 41, angle = 5, steepness = 11),
    JAVELIN(startHeight = 38, endHeight = 36, speed = 3, speedDelay = 2, delay = 42, angle = 1, steepness = 120),
    THROWN(startHeight = 40, endHeight = 36, speed = 5, speedDelay = 5, delay = 32, angle = 15, steepness = 11),
    CHINCHOMPA(startHeight = 40, endHeight = 36, speed = 5, speedDelay = 5, delay = 21, angle = 15, steepness = 11),
    MAGIC(startHeight = 43, endHeight = 31, speed = 10, speedDelay = 5, delay = 51, angle = 16, steepness = 64);

    fun createTemplate(id: Int): ProjectileTemplate = ProjectileTemplate(
        id, startHeight, endHeight, speed, speedDelay, delay, angle, steepness
    )
}

enum class MeleeCombatStance(val attack: Int = 0, val strength: Int = 0, val defence: Int = 0, val range: Int = 0) {
    ACCURATE(attack = 3),
    AGGRESSIVE(strength = 3),
    DEFENSIVE(defence = 3),
    CONTROLLED(attack = 1, strength = 1, defence = 1),
    RANGE_ACCURATE(range = 3),
    RAPID,
    LONGRANGE(defence = 3)
}

val Character.damageMultiplier: StatMultiplier by Property {
    StatMultiplier(
        attack = 1.0,
        range = 1.0,
        magic = 1.0,
        strength = 1.0,
        rangeStrength = 1.0,
        magicStrength = 1.0,
        defence = 1.0
    )
}

var Character.inCombatWith: Character? by Property { null }

internal fun StyleBonus.findBonus(attackType: AttackType): Int = when (attackType) {
    AttackType.STAB -> stab
    AttackType.SLASH -> slash
    AttackType.CRUSH -> crush
    AttackType.RANGED -> range
    AttackType.MAGIC -> magic
    AttackType.NONE -> throw IllegalCallerException("Can't attack without selecting attack style.")
}
