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

fun abs(n: ZoneUnit): ZoneUnit = abs(n.value).zones

val Int.zones: ZoneUnit get() = ZoneUnit(this)

inline class ZoneUnit(val value: Int) : Comparable<ZoneUnit> {
    val inTiles: TileUnit get() = TileUnit(value * SIZE_TILE.value)
    val inMapsquares: MapsquareUnit get() = MapsquareUnit(value / MapsquareUnit.SIZE_ZONE.value)
    val relativeMapSquare: ZoneUnit get() = ZoneUnit(value % MapsquareUnit.SIZE_ZONE.value)

    operator fun plus(other: ZoneUnit): ZoneUnit = ZoneUnit(value + other.value)
    operator fun minus(other: ZoneUnit): ZoneUnit = ZoneUnit(value - other.value)
    operator fun times(other: ZoneUnit): ZoneUnit = ZoneUnit(value * other.value)
    operator fun div(other: ZoneUnit): ZoneUnit = ZoneUnit(value / other.value)
    operator fun rem(other: ZoneUnit): ZoneUnit = ZoneUnit(value % other.value)
    operator fun inc(): ZoneUnit = ZoneUnit(value + 1)
    operator fun dec(): ZoneUnit = ZoneUnit(value - 1)
    operator fun unaryPlus(): ZoneUnit = this
    operator fun unaryMinus(): ZoneUnit = ZoneUnit(-value)
    operator fun rangeTo(other: ZoneUnit): ZoneUnitRange = ZoneUnitRange(this, other)
    override fun compareTo(other: ZoneUnit): Int = when {
        value < other.value -> -1
        value > other.value -> 1
        else -> 0
    }

    companion object {
        val SIZE_TILE: TileUnit = TileUnit(8)
    }
}

infix fun ZoneUnit.until(to: ZoneUnit): ZoneUnitRange = this..(to - 1.zones)

class ZoneUnitRange(
    start: ZoneUnit,
    endInclusive: ZoneUnit
) : ZoneUnitProgression(start, endInclusive, 1), ClosedRange<ZoneUnit> {
    override val start: ZoneUnit get() = first
    override val endInclusive: ZoneUnit get() = last
    override fun contains(value: ZoneUnit): Boolean = value.value in first.value..last.value
    override fun isEmpty(): Boolean = first > last
    override fun equals(other: Any?): Boolean = other is ZoneUnitRange &&
        (isEmpty() && other.isEmpty() || first == other.first && last == other.last)

    override fun hashCode(): Int = if (isEmpty()) -1 else (31 * first.value + last.value)
    override fun toString(): String = "$first..$last"
}

open class ZoneUnitProgression(start: ZoneUnit, endInclusive: ZoneUnit, var step: Int = 1) : Iterable<ZoneUnit> {
    init {
        require(step != 0) { "Step must be non-zero." }
        require(step != Int.MIN_VALUE) {
            "Step must be greater than Int.MIN_VALUE to avoid overflow on negation."
        }
    }

    val first: ZoneUnit = start
    val last: ZoneUnit = getProgressionLastElement(start, endInclusive, step)
    override fun iterator(): ZoneUnitProgressionIterator = ZoneUnitProgressionIterator(first, last, step)
    open fun isEmpty(): Boolean = if (step > 0) first > last else first < last
    override fun equals(other: Any?): Boolean = other is ZoneUnitProgression &&
        (isEmpty() && other.isEmpty() || first == other.first && last == other.last && step == other.step)

    override fun hashCode(): Int = if (isEmpty()) -1 else (31 * (31 * first.value + last.value) + step)
    override fun toString(): String = if (step > 0) "$first..$last step $step" else "$first downTo $last step ${-step}"
    infix fun step(step: TileUnit): ZoneUnitProgression = apply { this.step = step.inZones.value }
    infix fun step(step: ZoneUnit): ZoneUnitProgression = apply { this.step = step.value }
    infix fun step(step: MapsquareUnit): ZoneUnitProgression = apply { this.step = step.inZones.value }
}

class ZoneUnitProgressionIterator(first: ZoneUnit, last: ZoneUnit, private val step: Int) : Iterator<ZoneUnit> {
    private val finalElement = last
    private var hasNext = if (step > 0) first <= last else first >= last
    private var next = if (hasNext) first else finalElement
    override fun hasNext(): Boolean = hasNext
    override fun next(): ZoneUnit {
        val value = next
        if (value == finalElement) {
            if (!hasNext) throw NoSuchElementException()
            hasNext = false
        } else {
            next += step.zones
        }
        return value
    }
}

private fun getProgressionLastElement(start: ZoneUnit, end: ZoneUnit, step: Int): ZoneUnit = when {
    step > 0 -> if (start >= end) end else end - differenceModulo(end, start, step.zones)
    step < 0 -> if (start <= end) end else end + differenceModulo(start, end, (-step).zones)
    else -> throw IllegalArgumentException("Step is zero.")
}

private fun differenceModulo(a: ZoneUnit, b: ZoneUnit, c: ZoneUnit): ZoneUnit {
    val ac = a % c
    val bc = b % c
    return if (ac >= bc) ac - bc else ac - bc + c
}