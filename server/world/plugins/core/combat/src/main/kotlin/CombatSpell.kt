package io.guthix.oldscape.server.core.combat

import io.guthix.oldscape.server.template.ProjectileTemplate
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Character
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.entity.SpotAnimation

interface CombatSpell {
    val castAnim: Int
    val castSound: Int
    val castSpotAnim: SpotAnimation
    val impactSpotAnim: SpotAnimation
    val projectile: ProjectileTemplate
    val hit: (World, Player, Character) -> Int
}