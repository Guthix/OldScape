package io.guthix.oldscape.server.prayer

import io.guthix.oldscape.server.stat.StatMultiplier
import io.guthix.oldscape.server.world.entity.CharacterProperty
import io.guthix.oldscape.server.world.entity.Player

val Player.prayerMultiplier: StatMultiplier by CharacterProperty {
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