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

val Int.floors: FloorUnit get() = FloorUnit(this)

inline class FloorUnit(val value: Int) : Comparable<FloorUnit> {
    operator fun plus(other: FloorUnit): FloorUnit = FloorUnit(value + other.value)
    operator fun minus(other: FloorUnit): FloorUnit = FloorUnit(value - other.value)
    operator fun times(other: FloorUnit): FloorUnit = FloorUnit(value * other.value)
    operator fun div(other: FloorUnit): FloorUnit = FloorUnit(value / other.value)
    operator fun rem(other: FloorUnit): FloorUnit = FloorUnit(value % other.value)
    operator fun inc(): FloorUnit = FloorUnit(value + 1)
    operator fun dec(): FloorUnit = FloorUnit(value - 1)
    operator fun unaryPlus(): FloorUnit = this
    operator fun unaryMinus(): FloorUnit = FloorUnit(-value)
    operator fun rangeTo(other: FloorUnit): FloorUnitRange = FloorUnitRange(this, other)
    override fun compareTo(other: FloorUnit): Int = when {
        value < other.value -> -1
        value > other.value -> 1
        else -> 0
    }
}

class FloorUnitRange(
    start: FloorUnit,
    endInclusive: FloorUnit
) : FloorUnitProgression(start, endInclusive, 1), ClosedRange<FloorUnit> {
    override val start: FloorUnit get() = first
    override val endInclusive: FloorUnit get() = last
    override fun contains(value: FloorUnit): Boolean = value in first..last
    override fun isEmpty(): Boolean = first > last
    override fun equals(other: Any?): Boolean = other is FloorUnitRange &&
        (isEmpty() && other.isEmpty() || first == other.first && last == other.last)

    override fun hashCode(): Int = if (isEmpty()) -1 else (31 * first.value + last.value)
    override fun toString(): String = "$first..$last"
}

open class FloorUnitProgression(start: FloorUnit, endInclusive: FloorUnit, var step: Int = 1) : Iterable<FloorUnit> {
    init {
        require(step != 0) { "Step must be non-zero." }
        require(step != Int.MIN_VALUE) {
            "Step must be greater than Int.MIN_VALUE to avoid overflow on negation."
        }
    }

    val first: FloorUnit = start
    val last: FloorUnit = getProgressionLastElement(start, endInclusive, step)
    override fun iterator(): FloorUnitProgressionIterator = FloorUnitProgressionIterator(first, last, step)
    open fun isEmpty(): Boolean = if (step > 0) first > last else first < last
    override fun equals(other: Any?): Boolean = other is FloorUnitProgression &&
        (isEmpty() && other.isEmpty() || first == other.first && last == other.last && step == other.step)

    override fun hashCode(): Int = if (isEmpty()) -1 else (31 * (31 * first.value + last.value) + step)
    override fun toString(): String = if (step > 0) "$first..$last step $step" else "$first downTo $last step ${-step}"
    infix fun step(step: FloorUnit): FloorUnitProgression = apply { this.step = step.value }
}

class FloorUnitProgressionIterator(first: FloorUnit, last: FloorUnit, private val step: Int) : Iterator<FloorUnit> {
    private val finalElement = last
    private var hasNext = if (step > 0) first <= last else first >= last
    private var next = if (hasNext) first else finalElement
    override fun hasNext(): Boolean = hasNext
    override fun next(): FloorUnit {
        val value = next
        if (value == finalElement) {
            if (!hasNext) throw NoSuchElementException()
            hasNext = false
        } else {
            next += step.floors
        }
        return value
    }
}

private fun getProgressionLastElement(start: FloorUnit, end: FloorUnit, step: Int): FloorUnit = when {
    step > 0 -> if (start >= end) end else end - differenceModulo(end, start, step.floors)
    step < 0 -> if (start <= end) end else end + differenceModulo(start, end, (-step).floors)
    else -> throw IllegalArgumentException("Step is zero.")
}

private fun differenceModulo(a: FloorUnit, b: FloorUnit, c: FloorUnit): FloorUnit {
    val ac = a % c
    val bc = b % c
    return if (ac >= bc) ac - bc else ac - bc + c
}
