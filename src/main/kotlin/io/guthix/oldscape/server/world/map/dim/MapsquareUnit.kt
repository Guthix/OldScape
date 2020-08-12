/*
 * Copyright 2018-2020 Guthix
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.guthix.oldscape.server.world.map.dim

import kotlin.math.abs

fun abs(n: MapsquareUnit): MapsquareUnit = abs(n.value).mapsquares

val Int.mapsquares: MapsquareUnit get() = MapsquareUnit(this)

inline class MapsquareUnit(val value: Int) : Comparable<MapsquareUnit> {
    val inTiles: TileUnit get() = TileUnit(value * SIZE_TILE.value)
    val inZones: ZoneUnit get() = ZoneUnit(value * SIZE_ZONE.value)

    operator fun plus(other: MapsquareUnit): MapsquareUnit = MapsquareUnit(value + other.value)
    operator fun minus(other: MapsquareUnit): MapsquareUnit = MapsquareUnit(value - other.value)
    operator fun times(other: MapsquareUnit): MapsquareUnit = MapsquareUnit(value * other.value)
    operator fun div(other: MapsquareUnit): MapsquareUnit = MapsquareUnit(value / other.value)
    operator fun rem(other: MapsquareUnit): MapsquareUnit = MapsquareUnit(value % other.value)
    operator fun inc(): MapsquareUnit = MapsquareUnit(value + 1)
    operator fun dec(): MapsquareUnit = MapsquareUnit(value - 1)
    operator fun unaryPlus(): MapsquareUnit = this
    operator fun unaryMinus(): MapsquareUnit = MapsquareUnit(-value)
    operator fun rangeTo(other: MapsquareUnit): MapSquareUnitRange = MapSquareUnitRange(this, other)
    override fun compareTo(other: MapsquareUnit): Int = when {
        value < other.value -> -1
        value > other.value -> 1
        else -> 0
    }

    companion object {
        val SIZE_ZONE: ZoneUnit = ZoneUnit(8)
        val SIZE_TILE: TileUnit = TileUnit(ZoneUnit.SIZE_TILE.value * SIZE_ZONE.value)
    }
}

class MapSquareUnitRange(
    start: MapsquareUnit,
    endInclusive: MapsquareUnit
) : MapSquareUnitProgression(start, endInclusive, 1), ClosedRange<MapsquareUnit> {
    override val start: MapsquareUnit get() = first
    override val endInclusive: MapsquareUnit get() = last
    override fun contains(value: MapsquareUnit): Boolean = value.value in first.value..last.value
    override fun isEmpty(): Boolean = first > last
    override fun equals(other: Any?): Boolean = other is MapSquareUnitRange &&
        (isEmpty() && other.isEmpty() || first == other.first && last == other.last)

    override fun hashCode(): Int = if (isEmpty()) -1 else (31 * first.value + last.value)
    override fun toString(): String = "$first..$last"
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
    override fun iterator(): MapSquareUnitProgressionIterator = MapSquareUnitProgressionIterator(first, last, step)
    open fun isEmpty(): Boolean = if (step > 0) first > last else first < last
    override fun equals(other: Any?): Boolean = other is MapSquareUnitProgression &&
        (isEmpty() && other.isEmpty() || first == other.first && last == other.last && step == other.step)

    override fun hashCode(): Int = if (isEmpty()) -1 else (31 * (31 * first.value + last.value) + step)
    override fun toString(): String = if (step > 0) "$first..$last step $step" else "$first downTo $last step ${-step}"
    infix fun step(step: TileUnit): MapSquareUnitProgression = apply { this.step = step.inMapsquares.value }
    infix fun step(step: ZoneUnit): MapSquareUnitProgression = apply { this.step = step.inMapsquares.value }
    infix fun step(step: MapsquareUnit): MapSquareUnitProgression = apply { this.step = step.value }
}

class MapSquareUnitProgressionIterator(
    first: MapsquareUnit,
    last: MapsquareUnit,
    private val step: Int
) : Iterator<MapsquareUnit> {
    private val finalElement = last
    private var hasNext = if (step > 0) first <= last else first >= last
    private var next = if (hasNext) first else finalElement
    override fun hasNext(): Boolean = hasNext
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