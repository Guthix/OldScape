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
package io.guthix.oldscape.server.world.mapsquare.zone.tile

import io.guthix.oldscape.server.world.mapsquare.zone.ZoneDim

inline class TileDim(val dim: Int) {
    val zd get() = ZoneDim(dim / ZoneDim.SIZE_TILE.dim)

    operator fun plus(other: TileDim) = TileDim(dim + other.dim)
    operator fun minus(other: TileDim) = TileDim(dim - other.dim)
    operator fun times(other: TileDim) = TileDim(dim * other.dim)
    operator fun div(other: TileDim) = TileDim(dim / other.dim)
    operator fun rem(other: TileDim) = TileDim(dim % other.dim)
    operator fun unaryPlus() = this
    operator fun unaryMinus() = TileDim(-dim)
    operator fun rangeTo(other: TileDim) = TileDimRange(dim, other.dim)
}

class TileDimRange(override val start: Int, override val endInclusive: Int) : ClosedRange<Int>

val Int.td get() = TileDim(this)