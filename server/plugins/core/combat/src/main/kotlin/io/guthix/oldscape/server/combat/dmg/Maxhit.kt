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
package io.guthix.oldscape.server.combat.dmg

import io.guthix.oldscape.server.combat.currentStyle
import io.guthix.oldscape.server.combat.damageMultiplier
import io.guthix.oldscape.server.equipment.attackBonus
import io.guthix.oldscape.server.equipment.strengtBonus
import io.guthix.oldscape.server.prayer.prayerMultiplier
import io.guthix.oldscape.server.world.entity.Player
import kotlin.math.floor

private fun Player.effectiveMeleeStrength(): Double =
    (floor(stats.strength.status * prayerMultiplier.strength) + currentStyle.style.strengthBonus + 8) *
        damageMultiplier.strength

private fun Player.effectiveRangeStrength(): Double =
    (floor(stats.ranged.status * prayerMultiplier.rangeStrength) + currentStyle.style.rangeBonus + 8) *
        damageMultiplier.range

internal fun Player.maxMeleeHit(): Int =
    floor(0.5 + effectiveMeleeStrength() * (equipmentSet.strengtBonus.melee + 64.0) / 640.0).toInt()

internal fun Player.maxRangeHit(): Int =
    floor(0.5 + effectiveRangeStrength() * (equipmentSet.strengtBonus.range + 64.0) / 640.0).toInt()

internal fun Player.maxMagicHit(spellMaxHit: Int): Int = spellMaxHit * equipmentSet.attackBonus.magic
