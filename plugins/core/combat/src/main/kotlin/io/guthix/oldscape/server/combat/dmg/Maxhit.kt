package io.guthix.oldscape.server.combat.dmg

import io.guthix.oldscape.server.combat.attackStance
import io.guthix.oldscape.server.combat.damageMultiplier
import io.guthix.oldscape.server.prayer.prayerMultiplier
import io.guthix.oldscape.server.world.entity.Npc
import io.guthix.oldscape.server.world.entity.Player
import kotlin.math.floor

private fun Player.effectiveMeleeStrength(): Double =
    (floor(stats.strength.status * prayerMultiplier.strength) + attackStance.strength + 8) * damageMultiplier.strength

private fun Player.effectiveRangeStrength(): Double =
    (floor(stats.ranged.status * prayerMultiplier.rangeStrength) + attackStance.range + 8) * damageMultiplier.range

internal fun Player.maxMeleeHit(): Int =
    floor(0.5 + effectiveMeleeStrength() * (equipment.strengtBonus.melee + 64.0) / 640.0).toInt()

internal fun Player.maxRangeHit(): Int =
    floor(0.5 + effectiveRangeStrength() * (equipment.strengtBonus.range + 64.0) / 640.0).toInt()

internal fun Player.maxMagicHit(spellMaxHit: Int): Int = spellMaxHit * equipment.attackBonus.magic

internal fun Npc.maxHit(): Int = blueprint.maxHit ?: 0
