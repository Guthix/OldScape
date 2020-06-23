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
package io.guthix.oldscape.server.world.map

import io.guthix.oldscape.server.dimensions.FloorUnit
import io.guthix.oldscape.server.dimensions.TileUnit
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.sqrt

data class Tile(val floor: FloorUnit, val x: TileUnit, val y: TileUnit) {
    fun withInDistanceOf(other: Tile, distance: TileUnit): Boolean = if (floor == other.floor) {
        abs((other.x - x).value) <= distance.value && abs((other.y - y).value) <= distance.value
    } else {
        false
    }

    fun distanceTo(other: Tile): Int {
        val dx = x - other.x
        val dy = y - other.y
        return ceil(sqrt((dx * dx + dy * dy).value.toDouble())).toInt()
    }

    override fun toString(): String = "Tile(z=${floor.value}, x=${x.value}, y=${y.value})"
}


