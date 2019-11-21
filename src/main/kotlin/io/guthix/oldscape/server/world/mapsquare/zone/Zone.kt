/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.world.mapsquare.zone

import io.guthix.oldscape.server.world.mapsquare.FloorUnit
import io.guthix.oldscape.server.world.mapsquare.MapSquare
import io.guthix.oldscape.server.world.mapsquare.MapSquareFloor
import kotlin.math.abs

class Zone(val z: FloorUnit, val x: ZoneUnit, val y: ZoneUnit) {
    val inMapSquare = MapSquare(z, MapSquareFloor(x.inMapSquares, y.inMapSquares))

    fun withInDistanceOf(other: Zone, distance: ZoneUnit) = if (z == other.z) {
        abs((other.x - x).value) <= distance.value && abs((other.y - y).value) <= distance.value
    } else {
        false
    }

    override fun toString() = "Zone(z=${z.value}, x=${x.value}, y=${y.value})"
}