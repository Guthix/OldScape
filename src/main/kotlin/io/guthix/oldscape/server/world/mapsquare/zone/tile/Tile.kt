/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.world.mapsquare.zone.tile

import io.guthix.oldscape.server.world.mapsquare.FloorUnit
import kotlin.math.abs

data class Tile(val floor: FloorUnit, val x: TileUnit, val y: TileUnit) {
    fun withInDistanceOf(other: Tile, distance: TileUnit) = if (floor == other.floor) {
        abs((other.x - x).value) <= distance.value && abs((other.y - y).value) <= distance.value
    } else {
        false
    }

    override fun toString() = "Tile(z=${floor.value}, x=${x.value}, y=${y.value})"
}


