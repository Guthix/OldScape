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
package io.guthix.oldscape.server.dimensions

import kotlin.math.abs
import kotlin.math.max

fun max(a: TileUnit, b: TileUnit): TileUnit = max(a.value, b.value).tiles

fun abs(n: TileUnit): TileUnit = abs(n.value).tiles

val Int.tiles: TileUnit get() = TileUnit(this)

inline class TileUnit(val value: Int) : Comparable<TileUnit> {
    val inZones: ZoneUnit get() = ZoneUnit(value / ZoneUnit.SIZE_TILE.value)
    val inMapsquares: MapsquareUnit get() = MapsquareUnit(value / MapsquareUnit.SIZE_TILE.value)
    val relativeZone: TileUnit get() = TileUnit(value % ZoneUnit.SIZE_TILE.value)
    val relativeMapSquare: TileUnit get() = TileUnit(value % MapsquareUnit.SIZE_TILE.value)

    operator fun plus(other: TileUnit): TileUnit = TileUnit(value + other.value)
    operator fun minus(other: TileUnit): TileUnit = TileUnit(value - other.value)
    operator fun times(other: TileUnit): TileUnit = TileUnit(value * other.value)
    operator fun div(other: TileUnit): TileUnit = TileUnit(value / other.value)
    operator fun rem(other: TileUnit): TileUnit = TileUnit(value % other.value)
    operator fun inc(): TileUnit = TileUnit(value + 1)
    operator fun dec(): TileUnit = TileUnit(value - 1)
    operator fun unaryPlus(): TileUnit = this
    operator fun unaryMinus(): TileUnit = TileUnit(-value)
    operator fun rangeTo(other: TileUnit): TileUnitRange = TileUnitRange(this, other)
    override fun compareTo(other: TileUnit): Int = when {
        value < other.value -> -1
        value > other.value -> 1
        else -> 0
    }
}

infix fun TileUnit.until(to: TileUnit): TileUnitRange = this..(to - 1.tiles)

class TileUnitRange(
    start: TileUnit,
    endInclusive: TileUnit
) : TileUnitProgression(start, endInclusive, 1), ClosedRange<TileUnit> {
    override val start: TileUnit get() = first
    override val endInclusive: TileUnit get() = last
    override fun contains(value: TileUnit): Boolean = value.value in first.value..last.value
    override fun isEmpty(): Boolean = first > last
    override fun equals(other: Any?): Boolean = other is TileUnitRange &&
        (isEmpty() && other.isEmpty() || first == other.first && last == other.last)

    override fun hashCode(): Int = if (isEmpty()) -1 else (31 * first.value + last.value)
    override fun toString(): String = "$first..$last"
}

open class TileUnitProgression(start: TileUnit, endInclusive: TileUnit, var step: Int = 1) : Iterable<TileUnit> {
    init {
        require(step != 0) { "Step must be non-zero." }
        require(step != Int.MIN_VALUE) {
            "Step must be greater than Int.MIN_VALUE to avoid overflow on negation."
        }
    }

    val first: TileUnit = start
    val last: TileUnit = getProgressionLastElement(start, endInclusive, step)
    override fun iterator(): TileUnitProgressionIterator = TileUnitProgressionIterator(first, last, step)
    open fun isEmpty(): Boolean = if (step > 0) first > last else first < last
    override fun equals(other: Any?): Boolean = other is TileUnitProgression &&
        (isEmpty() && other.isEmpty() || first == other.first && last == other.last && step == other.step)

    override fun hashCode(): Int = if (isEmpty()) -1 else (31 * (31 * first.value + last.value) + step)
    override fun toString(): String = if (step > 0) "$first..$last step $step" else "$first downTo $last step ${-step}"
    infix fun step(step: TileUnit): TileUnitProgression = apply { this.step = step.value }
    infix fun step(step: ZoneUnit): TileUnitProgression = apply { this.step = step.inTiles.value }
    infix fun step(step: MapsquareUnit): TileUnitProgression = apply { this.step = step.inTiles.value }
}

class TileUnitProgressionIterator(first: TileUnit, last: TileUnit, private val step: Int) : Iterator<TileUnit> {
    private val finalElement = last
    private var hasNext = if (step > 0) first <= last else first >= last
    private var next = if (hasNext) first else finalElement
    override fun hasNext(): Boolean = hasNext
    override fun next(): TileUnit {
        val value = next
        if (value == finalElement) {
            if (!hasNext) throw NoSuchElementException()
            hasNext = false
        } else {
            next += step.tiles
        }
        return value
    }
}

private fun getProgressionLastElement(start: TileUnit, end: TileUnit, step: Int): TileUnit = when {
    step > 0 -> if (start >= end) end else end - differenceModulo(end, start, step.tiles)
    step < 0 -> if (start <= end) end else end + differenceModulo(start, end, (-step).tiles)
    else -> throw IllegalArgumentException("Step is zero.")
}

private fun differenceModulo(a: TileUnit, b: TileUnit, c: TileUnit): TileUnit {
    val ac = a % c
    val bc = b % c
    return if (ac >= bc) ac - bc else ac - bc + c
}