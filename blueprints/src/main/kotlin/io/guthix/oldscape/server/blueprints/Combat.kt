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
package io.guthix.oldscape.server.blueprints

enum class AttackType { STAB, SLASH, CRUSH, RANGED, MAGIC, NONE }

enum class AttackStyle {
    MELEE_ACCURATE,
    MELEE_AGGRESSIVE,
    MELEE_DEFENSIVE,
    MELEE_CONTROLLED,
    RANGE_ACCURATE,
    RANGE_RAPID,
    RANGE_LONGRANGE,
    MAGIC_ACCURATE,
    MAGIC_LONGRANGE,
    MAGIC_AUTOCAST,
    MAGIC_AUTOCAST_DEFENSIVE,
    NONE
}

class CombatStyle(val type: AttackType, val style: AttackStyle)

enum class WeaponType(val id: Int, val styles: List<CombatStyle>) {
    UNARMED(
        id = 0,
        styles = listOf(
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_ACCURATE), // punch
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE), // kick
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_DEFENSIVE) // block
        )
    ),
    AXE(
        id = 1,
        styles = listOf(
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_ACCURATE), // chop
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_AGGRESSIVE), // hack
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE), // smash
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_DEFENSIVE) // block
        )
    ),
    BLUNT(
        id = 2,
        styles = listOf(
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_ACCURATE), // pound
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE), // pummel
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE) // block
        )
    ),
    BOW(
        id = 3,
        styles = listOf(
            CombatStyle(AttackType.RANGED, AttackStyle.RANGE_ACCURATE), // accurate
            CombatStyle(AttackType.RANGED, AttackStyle.RANGE_RAPID), // rapid
            CombatStyle(AttackType.RANGED, AttackStyle.RANGE_LONGRANGE) // long range
        )
    ),
    CLAW(
        id = 4,
        styles = listOf(
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_ACCURATE), // chop
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_AGGRESSIVE), // slash
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_CONTROLLED), // lunge
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_DEFENSIVE) // block
        )
    ),
    CROSSBOW(
        id = 5,
        styles = listOf(
            CombatStyle(AttackType.RANGED, AttackStyle.RANGE_ACCURATE), // accurate
            CombatStyle(AttackType.RANGED, AttackStyle.RANGE_RAPID), // rapid
            CombatStyle(AttackType.RANGED, AttackStyle.RANGE_LONGRANGE) // long range
        )
    ),
    SALAMANDER(
        id = 6,
        styles = listOf(
            CombatStyle(AttackType.SLASH, AttackStyle.NONE), // scorch
            CombatStyle(AttackType.RANGED, AttackStyle.NONE), // flare
            CombatStyle(AttackType.MAGIC, AttackStyle.NONE) // blaze
        )
    ),
    CHINCHOMPA(
        id = 7,
        styles = listOf(
            CombatStyle(AttackType.RANGED, AttackStyle.RANGE_ACCURATE), // short fuse
            CombatStyle(AttackType.RANGED, AttackStyle.RANGE_RAPID), // medium fuse
            CombatStyle(AttackType.RANGED, AttackStyle.RANGE_LONGRANGE) // long fuse
        )
    ),
    GUN(
        id = 8,
        styles = listOf(
            CombatStyle(AttackType.NONE, AttackStyle.NONE), // aim and fire
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE) // kick
        )
    ),
    SLASHING_SWORD(
        id = 9,
        styles = listOf(
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_ACCURATE), // chop
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_AGGRESSIVE), // slash
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_CONTROLLED), // lunge
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_DEFENSIVE) // block
        )
    ),
    TWOHANDED_SWORD(
        id = 10,
        styles = listOf(
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_ACCURATE), // chop
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_AGGRESSIVE), // slash
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE), // smash
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_DEFENSIVE) // block
        )
    ),
    PICKAXE(
        id = 11,
        styles = listOf(
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_ACCURATE), // spike
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_AGGRESSIVE), // impale
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE), // smash
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_DEFENSIVE) // block
        )
    ),
    POLEARM(
        id = 12,
        styles = listOf(
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_CONTROLLED), // jab
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_AGGRESSIVE), // swipe
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_DEFENSIVE), // fend
        )
    ),
    POLESTAFF(
        id = 13,
        styles = listOf(
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_ACCURATE), // bash
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE), // pound
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_DEFENSIVE) // block
        )
    ),
    SCYTHE(
        id = 14,
        styles = listOf(
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_ACCURATE), // reap
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_AGGRESSIVE), // chop
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE), // job
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_DEFENSIVE) // block
        )
    ),
    SPEAR(
        id = 15,
        styles = listOf(
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_CONTROLLED), // lunge
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_CONTROLLED), // swipe
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_CONTROLLED), // pound
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_DEFENSIVE) // block
        )
    ),
    SPIKED_WEAPON(
        id = 16,
        styles = listOf(
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_ACCURATE), // pound
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE), // pummel
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_CONTROLLED), // spike
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_DEFENSIVE) // block
        )
    ),
    STABBING_SWORD(
        id = 17,
        styles = listOf(
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_ACCURATE), // stab
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_AGGRESSIVE), // lunge
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_AGGRESSIVE), // slash
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_DEFENSIVE) // block
        )
    ),
    STAFF(
        id = 18,
        styles = listOf(
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_ACCURATE), // bash
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE), // pound
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_DEFENSIVE), // focus
            CombatStyle(AttackType.MAGIC, AttackStyle.MAGIC_AUTOCAST), // spell
            CombatStyle(AttackType.MAGIC, AttackStyle.MAGIC_AUTOCAST_DEFENSIVE) // spell (defensive)
        )
    ),
    THROW_WEAPON(
        id = 19,
        styles = listOf(
            CombatStyle(AttackType.RANGED, AttackStyle.RANGE_ACCURATE), // accurate
            CombatStyle(AttackType.RANGED, AttackStyle.RANGE_RAPID), // rapid
            CombatStyle(AttackType.RANGED, AttackStyle.RANGE_LONGRANGE) // long range
        )
    ),
    WHIP(
        id = 20,
        styles = listOf(
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_ACCURATE), // flick
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_CONTROLLED), // lash
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_DEFENSIVE) // deflect
        )
    ),
    BLADED_STAFF(
        id = 21,
        styles = listOf(
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_ACCURATE), // jab
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_AGGRESSIVE), // swipe
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_DEFENSIVE), // fend
            CombatStyle(AttackType.MAGIC, AttackStyle.MAGIC_AUTOCAST), // spell
            CombatStyle(AttackType.MAGIC, AttackStyle.MAGIC_AUTOCAST_DEFENSIVE) // spell (defensive)
        )
    ),
    POWERED_STAFF(
        id = 23,
        styles = listOf(
            CombatStyle(AttackType.MAGIC, AttackStyle.MAGIC_ACCURATE), // accurate
            CombatStyle(AttackType.MAGIC, AttackStyle.MAGIC_ACCURATE), // accurate
            CombatStyle(AttackType.MAGIC, AttackStyle.MAGIC_LONGRANGE) // deflect
        )
    ),
    BANNER(
        id = 24,
        styles = listOf(
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_ACCURATE), // lunge
            CombatStyle(AttackType.SLASH, AttackStyle.MELEE_AGGRESSIVE), // swipe
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE), // pound
            CombatStyle(AttackType.STAB, AttackStyle.MELEE_DEFENSIVE), // block
        )
    ),
    BLUDGEON(
        id = 26,
        styles = listOf(
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE), // pound
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE), // pummel
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_AGGRESSIVE) // block
        )
    ),
    BULWARK(
        id = 27,
        styles = listOf(
            CombatStyle(AttackType.CRUSH, AttackStyle.MELEE_ACCURATE), // pummel
            CombatStyle(AttackType.NONE, AttackStyle.NONE) // block
        )
    ),
    BLASTER( // only available in easter events
        id = 0,
        styles = listOf(
            CombatStyle(AttackType.NONE, AttackStyle.NONE), // explosive
            CombatStyle(AttackType.NONE, AttackStyle.NONE) // flamer
        )
    )
}

data class CombatStats(
    val health: Int,
    val attack: Int,
    val strength: Int,
    val defence: Int,
    val range: Int,
    val magic: Int
)

data class CombatSequences(
    val spawn: Int? = null,
    val attack: Int,
    val defence: Int,
    val death: Int
)

data class StyleBonus(
    var stab: Int,
    var slash: Int,
    var crush: Int,
    var magic: Int,
    var range: Int
) {
    operator fun plus(value: StyleBonus?): StyleBonus {
        if (value == null) return this
        stab += value.stab
        slash += value.slash
        crush += value.crush
        magic += value.magic
        range += value.range
        return this
    }

    operator fun minus(value: StyleBonus?): StyleBonus {
        if (value == null) return this
        stab -= value.stab
        slash -= value.slash
        crush -= value.crush
        magic -= value.magic
        range -= value.range
        return this
    }
}

data class CombatBonus(
    var melee: Int,
    var range: Int,
    var magic: Int
) {
    operator fun plus(value: CombatBonus?): CombatBonus {
        if (value == null) return this
        melee += value.melee
        range += value.range
        magic += value.magic
        return this
    }

    operator fun minus(value: CombatBonus?): CombatBonus {
        if (value == null) return this
        melee -= value.melee
        range -= value.range
        magic -= value.magic
        return this
    }
}

