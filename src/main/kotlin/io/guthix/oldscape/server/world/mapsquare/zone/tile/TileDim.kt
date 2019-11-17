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

inline class TileDim(val dim: Int): Comparable<TileDim> {
    val zd get() = ZoneDim(dim / ZoneDim.SIZE_TILE.dim)

    operator fun plus(other: TileDim) = TileDim(dim + other.dim)
    operator fun minus(other: TileDim) = TileDim(dim - other.dim)
    operator fun times(other: TileDim) = TileDim(dim * other.dim)
    operator fun div(other: TileDim) = TileDim(dim / other.dim)
    operator fun rem(other: TileDim) = TileDim(dim % other.dim)
    operator fun unaryPlus() = this
    operator fun unaryMinus() = TileDim(-dim)
    operator fun rangeTo(other: TileDim) = TileDimProgression(this, other)

    override fun compareTo(other: TileDim): Int = when {
        dim < other.dim -> -1
        dim > other.dim -> 1
        else -> 0
    }
}

class TileDimIterator(
    start: TileDim,
    private val endInclusive: TileDim,
    private val step: TileDim
) : Iterator<TileDim> {
    private var current = start
    override fun hasNext() = current <= endInclusive
    override fun next() = current + step
}

class TileDimProgression(
    override val start: TileDim,
    override val endInclusive: TileDim,
    private val step: TileDim = 1.td
) : Iterable<TileDim>, ClosedRange<TileDim> {
    override fun iterator() = TileDimIterator(start, endInclusive, step)
    infix fun step(tiles: TileDim) = TileDimIterator(start, endInclusive, tiles)
}

val Int.td get() = TileDim(this)