/**
 * This file is part of Guthix OldScape-Server.
 *
 * Guthix OldScape-Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.combat

import io.guthix.oldscape.server.blueprints.AttackStyle
import io.guthix.oldscape.server.blueprints.CombatSequences
import io.guthix.oldscape.server.blueprints.StyleBonus
import io.guthix.oldscape.server.stat.StatMultiplier
import io.guthix.oldscape.server.world.entity.Character
import io.guthix.oldscape.server.world.entity.CharacterProperty
import io.guthix.oldscape.server.world.entity.Npc
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

val Character.damageMultiplier: StatMultiplier by CharacterProperty {
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

var Npc.attackSpeed: Int by CharacterProperty { blueprint.attackSpeed }

val Npc.combatSequences: CombatSequences? by CharacterProperty { blueprint.combatSequences }

val Player.attackSpeed: Int by CharacterProperty { 5 } // TODO

val Player.combatSequences: CombatSequences by CharacterProperty {
    CombatSequences(
        attack = 422,
        defence = -1, // TODO
        death = -1, // TODO
    )
}

var Character.inCombatWith: Character? by CharacterProperty { null }

val Character.attackStance: MeleeCombatStance by CharacterProperty {
    MeleeCombatStance.ACCURATE
}

val Character.attackStyle: AttackStyle by CharacterProperty {
    AttackStyle.STAB
}

internal fun StyleBonus.findMeleeBonus(attackStyle: AttackStyle): Int = when (attackStyle) {
    AttackStyle.STAB -> stab
    AttackStyle.SLASH -> slash
    AttackStyle.CRUSH -> crush
    else -> throw IllegalCallerException("Attack style must be a melee style.")
}

internal fun StyleBonus.findBonus(attackStyle: AttackStyle): Int = when (attackStyle) {
    AttackStyle.STAB -> stab
    AttackStyle.SLASH -> slash
    AttackStyle.CRUSH -> crush
    AttackStyle.RANGED -> range
    AttackStyle.MAGIC -> magic
    AttackStyle.NONE -> throw IllegalCallerException("Can't attack without selecting attack style.")
}
