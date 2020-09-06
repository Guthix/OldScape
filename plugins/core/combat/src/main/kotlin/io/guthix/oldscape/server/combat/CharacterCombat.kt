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

import io.guthix.oldscape.server.Property
import io.guthix.oldscape.server.template.SequenceTemplates
import io.guthix.oldscape.server.world.map.dim.TileUnit
import io.guthix.oldscape.server.world.map.dim.max
import io.guthix.oldscape.server.world.map.dim.tiles
import io.guthix.oldscape.server.stat.StatMultiplier
import io.guthix.oldscape.server.template.*
import io.guthix.oldscape.server.template.SequenceTemplate
import io.guthix.oldscape.server.world.entity.Character
import io.guthix.oldscape.server.world.entity.Player

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

val Player.selectedTypes: IntArray by Property {
    IntArray(WeaponType.values().size)
}

val Player.currentStyle: CombatStyle
    get() {
        val weaponType = equipmentSet.weapon?.weaponType ?: WeaponType.UNARMED
        val index = selectedTypes[weaponType.ordinal]
        return weaponType.styles[index]
    }

val Player.attackSpeed: Int get() = equipmentSet.weapon?.baseAttackSpeed?.plus(currentStyle.style.attackSpeedBonus) ?: 1

val Player.attackRange: TileUnit get() = max(
    10.tiles, equipmentSet.weapon?.baseAttackRange?.plus(currentStyle.style.attackRangeBonus.tiles) ?: 1.tiles
)

val Player.attackSequence: SequenceTemplate by Property {
    equipmentSet.weapon?.attackAnim ?: SequenceTemplates[422]
}

val Player.defenceSequence: SequenceTemplate by Property {
    equipmentSet.weapon?.blockAnim ?: SequenceTemplates[424]
}

val Player.deathSequence: Int by Property { 836 }

var Character.inCombatWith: Character? by Property { null }

internal fun StyleBonus.findBonus(attackType: AttackType): Int = when (attackType) {
    AttackType.STAB -> stab
    AttackType.SLASH -> slash
    AttackType.CRUSH -> crush
    AttackType.RANGED -> range
    AttackType.MAGIC -> magic
    AttackType.NONE -> throw IllegalCallerException("Can't attack without selecting attack style.")
}
