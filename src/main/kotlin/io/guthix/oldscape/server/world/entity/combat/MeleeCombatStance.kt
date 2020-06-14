package io.guthix.oldscape.server.world.entity.combat

enum class MeleeCombatStance(val attack: Int = 0, val strength: Int = 0, val defence: Int = 0, val range: Int = 0) {
    ACCURATE(attack = 3),
    AGGRESSIVE(strength = 3),
    DEFENSIVE(defence = 3),
    CONTROLLED(attack = 1, strength = 1, defence = 1),
    RANGE_ACCURATE(range = 3),
    RAPID,
    LONGRANGE(defence = 3)
}