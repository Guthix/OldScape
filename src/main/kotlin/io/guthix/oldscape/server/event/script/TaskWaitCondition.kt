/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.event.script

import kotlin.coroutines.Continuation

interface TaskWaitCondition {
    fun canResume(): Boolean
}

class TickCondition(private var tickCount: Int) : TaskWaitCondition {
    override fun canResume() = --tickCount == 0
}

class LambdaCondition(private val cond: () -> Boolean) : TaskWaitCondition {
    override fun canResume() = cond.invoke()
}

object TrueCondition : TaskWaitCondition { override fun canResume() = true }

class ConditionalContinuation(
    val condition: TaskWaitCondition,
    val continuation: Continuation<Unit>
) : TaskWaitCondition by condition