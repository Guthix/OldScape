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
package io.guthix.oldscape.server.world.mapsquare

import io.guthix.oldscape.server.world.mapsquare.zone.ZoneDim
import io.guthix.oldscape.server.world.mapsquare.zone.ZoneDimRange
import io.guthix.oldscape.server.world.mapsquare.zone.tile.TileDim

inline class MapSquareDim(val dim: Int) {
    val zd get() = ZoneDim(dim * SIZE_ZONE.dim)

    val td get() = TileDim(dim * SIZE_TILE.dim)

    operator fun plus(other: MapSquareDim) = MapSquareDim(dim + other.dim)
    operator fun minus(other: MapSquareDim) = MapSquareDim(dim - other.dim)
    operator fun times(other: MapSquareDim) = MapSquareDim(dim * other.dim)
    operator fun div(other: MapSquareDim) = MapSquareDim(dim / other.dim)
    operator fun rem(other: MapSquareDim) = MapSquareDim(dim % other.dim)
    operator fun unaryPlus() = this
    operator fun unaryMinus() = MapSquareDim(-dim)
    operator fun rangeTo(other: ZoneDim) = MapSquareDimRange(dim, other.dim)

    companion object {
        val SIZE_ZONE = ZoneDim(8)
        val SIZE_TILE = TileDim(ZoneDim.SIZE_TILE.dim * SIZE_ZONE.dim)
    }
}

class MapSquareDimRange(override val start: Int, override val endInclusive: Int) : ClosedRange<Int>

val Int.md get() = MapSquareDim(this)