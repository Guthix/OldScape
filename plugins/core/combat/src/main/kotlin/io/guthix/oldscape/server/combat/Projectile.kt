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
package io.guthix.oldscape.server.combat

import io.guthix.oldscape.server.world.entity.Character
import io.guthix.oldscape.server.world.entity.Projectile
import io.guthix.oldscape.server.world.map.Tile

class Arrow(
    id: Int,
    start: Tile,
    target: Character
) : Projectile(
    id,
    start,
    startHeight = 40,
    target,
    targetHeight = 36,
    speed = 5,
    speedDelay = 5,
    delay = 41,
    angle = 15,
    steepness = 11
)