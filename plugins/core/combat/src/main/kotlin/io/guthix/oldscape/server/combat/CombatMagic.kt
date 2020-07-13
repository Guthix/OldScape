/*
 * This file is part of Guthix OldScape-Server.
 *
 * Guthix OldScape-Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Guthix OldScape-Server. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.combat

import io.guthix.oldscape.server.combat.type.magicAttack
import io.guthix.oldscape.server.event.IfOnNpcEvent
import io.guthix.oldscape.server.plugin.Script
import io.guthix.oldscape.server.world.entity.Character
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.entity.Projectile
import io.guthix.oldscape.server.world.map.Tile

data class CombatSpell(
    val spotAnimationId: Int,
    val spotAnimationHeight: Int,
    val projectileId: Int,
    val startHeight: Int,
    val targetHeight: Int,
    val speed: Int,
    val speedDelay: Int,
    val delay: Int,
    val angle: Int,
    val steepness: Int,
    val maxHit: Player.() -> Int
) {
    fun createProjectile(from: Tile, to: Character): Projectile =
        Projectile(projectileId, from, startHeight, to, targetHeight, speed, speedDelay, delay, angle, steepness)
}

fun Script.registerCombatSpell(interfaceId: Int, interfaceSlotId: Int, spell: CombatSpell) {
    on(IfOnNpcEvent::class).where { this.interfaceId == interfaceId && this.interfaceSlotId == interfaceSlotId }.then {
        player.magicAttack(npc, world, spell)
    }
}