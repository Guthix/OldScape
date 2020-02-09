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

import io.guthix.oldscape.server.world.mapsquare.zone.ZoneUnit
import io.guthix.oldscape.server.world.mapsquare.zone.tile.TileUnit
import kotlin.math.abs

fun abs(n: MapsquareUnit) = abs(n.value).mapsquares

val Int.mapsquares get() = MapsquareUnit(this)

inline class MapsquareUnit(val value: Int): Comparable<MapsquareUnit> {
    val inTiles get() = TileUnit(value * SIZE_TILE.value)
    val inZones get() = ZoneUnit(value * SIZE_ZONE.value)

    operator fun plus(other: MapsquareUnit) = MapsquareUnit(value + other.value)
    operator fun minus(other: MapsquareUnit) = MapsquareUnit(value - other.value)
    operator fun times(other: MapsquareUnit) = MapsquareUnit(value * other.value)
    operator fun div(other: MapsquareUnit) = MapsquareUnit(value / other.value)
    operator fun rem(other: MapsquareUnit) = MapsquareUnit(value % other.value)
    operator fun inc() = MapsquareUnit(value + 1)
    operator fun dec() = MapsquareUnit(value - 1)
    operator fun unaryPlus() = this
    operator fun unaryMinus() = MapsquareUnit(-value)
    operator fun rangeTo(other: MapsquareUnit) = MapSquareUnitRange(this, other)
    override fun compareTo(other: MapsquareUnit): Int = when {
        value < other.value -> -1
        value > other.value -> 1
        else -> 0
    }

    companion object {
        val SIZE_ZONE = ZoneUnit(8)
        val SIZE_TILE = TileUnit(ZoneUnit.SIZE_TILE.value * SIZE_ZONE.value)
    }
}

class MapSquareUnitRange(
    start: MapsquareUnit,
    endInclusive: MapsquareUnit
) : MapSquareUnitProgression(start, endInclusive, 1), ClosedRange<MapsquareUnit> {
    override val start get() = first
    override val endInclusive get() = last
    override fun contains(value: MapsquareUnit): Boolean = value.value in first.value..last.value
    override fun isEmpty() = first > last
    override fun equals(other: Any?) = other is MapSquareUnitRange &&
        (isEmpty() && other.isEmpty() || first == other.first && last == other.last)
    override fun hashCode() = if (isEmpty()) -1 else (31 * first.value + last.value)
    override fun toString() = "$first..$last"
}

open class MapSquareUnitProgression(
    start: MapsquareUnit,
    endInclusive: MapsquareUnit,
    var step: Int = 1
) : Iterable<MapsquareUnit> {
    init {
        require(step != 0) { "Step must be non-zero." }
        require(step != Int.MIN_VALUE) {
            "Step must be greater than Int.MIN_VALUE to avoid overflow on negation."
        }
    }
    val first: MapsquareUnit = start
    val last: MapsquareUnit = getProgressionLastElement(start, endInclusive, step)
    override fun iterator() = MapSquareUnitProgressionIterator(first, last, step)
    open fun isEmpty() = if (step > 0) first > last else first < last
    override fun equals(other: Any?) = other is MapSquareUnitProgression &&
        (isEmpty() && other.isEmpty() || first == other.first && last == other.last && step == other.step)
    override fun hashCode() = if (isEmpty()) -1 else (31 * (31 * first.value + last.value) + step)
    override fun toString() = if (step > 0) "$first..$last step $step" else "$first downTo $last step ${-step}"
    infix fun step(step: TileUnit) = this.apply { this.step = step.inMapsquares.value }
    infix fun step(step: ZoneUnit) = this.apply { this.step = step.inMapsquares.value }
    infix fun step(step: MapsquareUnit) = this.apply { this.step = step.value }
}

class MapSquareUnitProgressionIterator(
    first: MapsquareUnit,
    last: MapsquareUnit,
    private val step: Int
) : Iterator<MapsquareUnit> {
    private val finalElement = last
    private var hasNext = if (step > 0) first <= last else first >= last
    private var next = if (hasNext) first else finalElement
    override fun hasNext() = hasNext
    override fun next(): MapsquareUnit {
        val value = next
        if (value == finalElement) {
            if (!hasNext) throw NoSuchElementException()
            hasNext = false
        } else {
            next += step.mapsquares
        }
        return value
    }
}

private fun getProgressionLastElement(start: MapsquareUnit, end: MapsquareUnit, step: Int): MapsquareUnit = when {
    step > 0 -> if (start >= end) end else end - differenceModulo(end, start, step.mapsquares)
    step < 0 -> if (start <= end) end else end + differenceModulo(start, end, (-step).mapsquares)
    else -> throw IllegalArgumentException("Step is zero.")
}

private fun differenceModulo(a: MapsquareUnit, b: MapsquareUnit, c: MapsquareUnit): MapsquareUnit {
    val ac = a % c
    val bc = b % c
    return if (ac >= bc) ac - bc else ac - bc + c
}