package io.guthix.oldscape.server.combat

import io.guthix.oldscape.server.blueprints.AttackStyle
import io.guthix.oldscape.server.stat.StatMultiplier
import io.guthix.oldscape.server.world.entity.CharacterProperty
import io.guthix.oldscape.server.world.entity.Character
import io.guthix.oldscape.server.world.entity.Npc

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

val Character.attackStance: MeleeCombatStance by CharacterProperty {
    MeleeCombatStance.ACCURATE
}

val Character.attackStyle: AttackStyle by CharacterProperty {
    AttackStyle.STAB
}