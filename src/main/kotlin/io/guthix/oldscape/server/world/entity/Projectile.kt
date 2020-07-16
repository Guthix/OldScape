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
package io.guthix.oldscape.server.world.entity

import io.guthix.oldscape.server.blueprints.ProjectileBlueprint
import io.guthix.oldscape.server.world.map.Tile

class Projectile(private val bp: ProjectileBlueprint, val start: Tile, val target: Character) {
    val id: Int get() = bp.id

    val startHeight: Int get() = bp.startHeight

    val endHeight: Int get() = bp.endHeight

    val speed: Int get() = bp.speed

    val speedDelay: Int get() = bp.speedDelay

    val delay: Int get() = bp.delay

    val angle: Int get() = bp.angle

    val steepness: Int get() = bp.steepness

    val distance: Int = start.distanceTo(target.pos)

    val lifetimeClientTicks: Int = bp.delay + bp.speedDelay + bp.speed * distance

    val lifeTimeServerTicks: Int = lifetimeClientTicks / 30
}