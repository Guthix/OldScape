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

import io.guthix.oldscape.server.world.mapsquare.MapSquareDim
import io.guthix.oldscape.server.world.mapsquare.zone.tile.TileDim

inline class ZoneDim(val dim: Int): Comparable<ZoneDim> {
    val td get() = TileDim(dim * SIZE_TILE.dim)
    val md get() = MapSquareDim(dim / MapSquareDim.SIZE_ZONE.dim)

    operator fun plus(other: ZoneDim) = ZoneDim(dim + other.dim)
    operator fun minus(other: ZoneDim) = ZoneDim(dim - other.dim)
    operator fun times(other: ZoneDim) = ZoneDim(dim * other.dim)
    operator fun div(other: ZoneDim) = ZoneDim(dim / other.dim)
    operator fun rem(other: ZoneDim) = ZoneDim(dim % other.dim)
    operator fun unaryPlus() = this
    operator fun unaryMinus() = ZoneDim(-dim)
    operator fun rangeTo(other: ZoneDim) = ZoneDimProgression(this, other)

    override fun compareTo(other: ZoneDim): Int = when {
        dim < other.dim -> -1
        dim > other.dim -> 1
        else -> 0
    }

    companion object {
        val SIZE_TILE = TileDim(8)
    }
}

class ZoneDimIterator(
    start: ZoneDim,
    private val endInclusive: ZoneDim,
    private val step: ZoneDim
) : Iterator<ZoneDim> {
    private var current = start
    override fun hasNext() = current <= endInclusive
    override fun next() = current + step
}

class ZoneDimProgression(
    override val start: ZoneDim,
    override val endInclusive: ZoneDim,
    private val step: ZoneDim = 1.zd
) : Iterable<ZoneDim>, ClosedRange<ZoneDim> {
    override fun iterator() = ZoneDimIterator(start, endInclusive, step)
    infix fun step(zones: ZoneDim) = ZoneDimIterator(start, endInclusive, zones)
}

val Int.zd get() = ZoneDim(this)