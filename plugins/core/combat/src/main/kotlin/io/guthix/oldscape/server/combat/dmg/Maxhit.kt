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
package io.guthix.oldscape.server.combat.dmg

import io.guthix.oldscape.server.combat.currentStyle
import io.guthix.oldscape.server.combat.damageMultiplier
import io.guthix.oldscape.server.prayer.prayerMultiplier
import io.guthix.oldscape.server.world.entity.Npc
import io.guthix.oldscape.server.world.entity.Player
import kotlin.math.floor

private fun Player.effectiveMeleeStrength(): Double =
    (floor(stats.strength.status * prayerMultiplier.strength) + currentStyle.style.strengthBonus + 8) *
        damageMultiplier.strength

private fun Player.effectiveRangeStrength(): Double =
    (floor(stats.ranged.status * prayerMultiplier.rangeStrength) + currentStyle.style.rangeBonus + 8) *
        damageMultiplier.range

internal fun Player.maxMeleeHit(): Int =
    floor(0.5 + effectiveMeleeStrength() * (equipment.strengtBonus.melee + 64.0) / 640.0).toInt()

internal fun Player.maxRangeHit(): Int =
    floor(0.5 + effectiveRangeStrength() * (equipment.strengtBonus.range + 64.0) / 640.0).toInt()

internal fun Player.maxMagicHit(spellMaxHit: Int): Int = spellMaxHit * equipment.attackBonus.magic

internal fun Npc.maxHit(): Int = blueprint.maxHit ?: 0
