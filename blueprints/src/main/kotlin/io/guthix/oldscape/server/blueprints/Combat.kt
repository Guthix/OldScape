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

enum class AttackStyle { STAB, SLASH, CRUSH, RANGED, MAGIC, NONE }

enum class WeaponType {
    AXE,
    BLUNT,
    BULWARK,
    CLAW,
    POLEARM,
    PICKAXE,
    SCYTHE,
    SLASHING_SWORD,
    SPEAR,
    SPIKED_WEAPON,
    STABBING_SWORD,
    TWOHANDED_SWORD,
    WHIP,
    BOW,
    CHINCHOMPA,
    CROSSBOW,
    THROW_WEAPON,
    STAFF,
    BLADED_STAFF,
    POWERED_STAFF,
    BANNER,
    BLASTER,
    GUN,
    POLE_STAFF,
    SALAMANDER,
    UNARMED
}

class CombatStats(
    val health: Int,
    val attack: Int,
    val strength: Int,
    val defence: Int,
    val range: Int,
    val magic: Int
)

class CombatSequences(
    val spawn: Int? = null,
    val attack: Int,
    val defence: Int,
    val death: Int
)

class StyleBonus(
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

class CombatBonus(
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

