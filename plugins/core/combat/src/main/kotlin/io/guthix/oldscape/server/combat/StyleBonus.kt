package io.guthix.oldscape.server.combat

import io.guthix.oldscape.server.blueprints.AttackStyle
import io.guthix.oldscape.server.blueprints.StyleBonus

internal fun StyleBonus.findMeleeBonus(attackStyle: AttackStyle): Int = when(attackStyle) {
    AttackStyle.STAB -> stab
    AttackStyle.SLASH -> slash
    AttackStyle.CRUSH -> crush
    else -> throw IllegalCallerException("Attack style must be a melee style.")
}

internal fun StyleBonus.findBonus(attackStyle: AttackStyle): Int = when(attackStyle) {
    AttackStyle.STAB -> stab
    AttackStyle.SLASH -> slash
    AttackStyle.CRUSH -> crush
    AttackStyle.RANGED -> range
    AttackStyle.MAGIC -> magic
    AttackStyle.NONE -> throw IllegalCallerException("Can't attack without selecting attack style.")
}
