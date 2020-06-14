package io.guthix.oldscape.server.combat

import io.guthix.oldscape.server.world.entity.Player
import kotlin.math.floor

private fun Player.effectiveAttack() =
    (floor(stats.attack.status * prayerBonus.attack) + stance.attack + 8) * equipment.multiplierBonus.attack

private fun Player.effectiveMeleeStrength() =
    (floor(stats.strength.status * prayerBonus.strength) + stance.strength + 8) * equipment.multiplierBonus.strength

private fun Player.effectiveDefence() =
    (floor(stats.defence.status * prayerBonus.defence) + stance.defence + 8) * equipment.multiplierBonus.defence

private fun Player.effectiveRange() =
    (floor(stats.ranged.status * prayerBonus.range) + stance.range + 8) * equipment.multiplierBonus.defence

private fun Player.effectiveMgic() =
    (floor(stats.magic.status * prayerBonus.magic) + 8) * equipment.multiplierBonus.magic

private fun Player.maxMeleeHit() =
    floor(0.5 * effectiveMeleeStrength() + (equipment.strengtBonus.melee + 64.0) / 640.0)

// TODO get correct attackBonus based on attackStyle
private fun Player.maxAttackRol(): Double = effectiveAttack() * (equipment.attackBonus.slash + 64)

private fun Player.maxDefenceRol(): Double = effectiveDefence() * (equipment.defenceBonus.slash + 64)

// TODO make character
fun Player.accuracy(other: Player): Double {
    val attackRoll = maxAttackRol()
    val defenceRoll = other.maxDefenceRol()
    return if(attackRoll > defenceRoll) 1 - (defenceRoll + 2) / (2*(attackRoll + 1))
    else attackRoll / (2 * (defenceRoll + 1))
}