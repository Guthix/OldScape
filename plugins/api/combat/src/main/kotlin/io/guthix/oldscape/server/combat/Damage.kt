package io.guthix.oldscape.server.combat

import io.guthix.oldscape.server.world.entity.Player

fun Player.effectiveMeleeStrength() {
    stats.strength.status + equipment.strengtBonus.melee
}
