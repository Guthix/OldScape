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
package io.guthix.oldscape.server.action

import kotlin.coroutines.Continuation

interface ActionCondition {
    fun canResume(): Boolean
}

class TickCondition(private var tickCount: Int) : ActionCondition {
    override fun canResume() = --tickCount == 0
}

class LambdaCondition(private val cond: () -> Boolean) : ActionCondition {
    override fun canResume() = cond.invoke()
}

object InitialCondition : ActionCondition {
    override fun canResume() = true
}

class ConditionalContinuation(
    val condition: ActionCondition,
    val continuation: Continuation<Unit>
) : ActionCondition by condition