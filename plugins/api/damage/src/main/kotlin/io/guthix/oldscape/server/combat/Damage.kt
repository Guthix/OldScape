package io.guthix.oldscape.server.combat

import io.guthix.oldscape.server.blueprints.AttackStyle
import io.guthix.oldscape.server.world.entity.Character
import io.guthix.oldscape.server.world.entity.Player
import kotlin.math.floor
import kotlin.random.Random

private fun Character.effectiveAttack() =
    (floor(attackStat * prayerBonus.attack) + attackStance.attack + 8) * damageMultiplier.attack

private fun Character.effectiveDefence() =
    (floor(defenceStat * prayerBonus.defence) + attackStance.defence + 8) * damageMultiplier.defence

private fun Character.effectiveRange() =
    (floor(rangeStat * prayerBonus.range) + attackStance.range + 8) * damageMultiplier.strength

private fun Character.effectiveMagic() =
    (floor(magicStat * prayerBonus.magic) + 8) * damageMultiplier.magic

private fun Character.effectiveMeleeStrength() =
    (floor(strengthStat * prayerBonus.strength) + attackStance.strength + 8) * damageMultiplier.strength

private fun Character.effectiveRangeStrength() =
    (floor(rangeStat * prayerBonus.range) + attackStance.range + 8) * damageMultiplier.range

private fun Character.effectiveMagicStrength() = (floor(magicStat * prayerBonus.magic) + 8) * damageMultiplier.magic

fun Character.maxMeleeHit(): Int =
    floor(0.5 + effectiveMeleeStrength() * (strengthBonus.melee + 64.0) / 640.0).toInt()

fun Character.maxRangeHit(): Int =
    floor(0.5 + effectiveRangeStrength() * (strengthBonus.range + 64.0) / 640.0).toInt()

fun Character.maxMagicHit(): Int =
    floor(0.5 + effectiveMagicStrength() * (strengthBonus.magic + 64.0) / 640.0).toInt()

private fun Character.maxAttackRol(): Double = effectiveAttack() * (attackBonus + 64)

private fun Character.maxRangeRol(): Double = effectiveRange() * (attackBonus + 64)

private fun Character.maxMagicRol(): Double = effectiveMagic() * (attackBonus + 64)

private fun Character.maxDefenceRol(attackStyle: AttackStyle): Double =
    effectiveDefence() * (defenceBonus.findByStyle(attackStyle) + 64)

private fun Character.accuracy(other: Character): Double {
    val attackRoll = maxAttackRol()
    val defenceRoll = other.maxDefenceRol(attackStyle)
    return if(attackRoll > defenceRoll) 1 - (defenceRoll + 2) / (2 * (attackRoll + 1))
    else attackRoll / (2 * (defenceRoll + 1))
}

fun Player.calcHit(other: Character): Int? {
    val accuracy = accuracy(other)
    val maxHit = maxMeleeHit()
    return if(Random.nextInt(1) < accuracy) {
        Random.nextInt(maxHit)
    } else null
}