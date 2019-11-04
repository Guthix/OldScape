/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.cache.model

import kotlin.math.sqrt

class VertexNormal {
    var x: Int = 0
    var y: Int = 0
    var z: Int = 0
    var magnitude: Int = 0

    fun normalize(): Vec3f {
        var length = sqrt((x * x + y * y + z * z).toDouble())
        if (length == 0.toDouble()) {
            length = 1.toDouble()
        }
        return Vec3f((x / length).toFloat(), (y / length).toFloat(), (z / length).toFloat())
    }
}