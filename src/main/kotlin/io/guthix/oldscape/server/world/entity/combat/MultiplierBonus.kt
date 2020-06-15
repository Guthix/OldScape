package io.guthix.oldscape.server.world.entity.combat

import io.guthix.oldscape.server.blueprints.AttackStyle

class MultiplierBonus(
    var attack: Double,
    var strength: Double,
    val defence: Double,
    var range: Double,
    var magic: Double
) {
    fun findByStyle(attackStyle: AttackStyle): Double = when(attackStyle) {
        AttackStyle.STAB, AttackStyle.SLASH, AttackStyle.CRUSH -> attack
        AttackStyle.RANGED -> range
        AttackStyle.MAGIC -> magic
        AttackStyle.NONE -> 0.0
    }
}