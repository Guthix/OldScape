/**
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
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.world.entity

import io.guthix.oldscape.server.world.map.Tile

open class Projectile(
    val id: Int,
    val start: Tile,
    val startHeight: Int,
    val target: Character,
    val targetHeight: Int,
    val speed: Int,
    val speedDelay: Int,
    val delay: Int,
    val angle: Int,
    val steepness: Int
) {
    val distance: Int = start.distanceTo(target.pos)

    val lifetime: Int = delay + speedDelay + speed * distance
}