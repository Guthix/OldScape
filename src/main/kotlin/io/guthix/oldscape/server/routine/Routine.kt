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
package io.guthix.oldscape.server.routine

import io.guthix.oldscape.server.world.entity.character.player.Player
import kotlin.coroutines.*
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn

open class Routine(val type: Type, val player: Player) : Continuation<Unit>, Comparable<Routine> {
    abstract class Type(val priority: Int) : Comparable<Type> {
        override fun compareTo(other: Type) = when {
            priority < other.priority -> -1
            priority > other.priority -> 1
            else -> 0
        }
    }
    internal var cancelAction: () -> Unit = {}

    internal var next: ConditionalContinuation? = null

    override val context: CoroutineContext = EmptyCoroutineContext

    override fun resumeWith(result: Result<Unit>) { }

    internal fun resumeIfPossible()  = next?.let {
        if(it.canResume()) {
            player.routines.remove(type)
            it.continuation.resume(Unit)
        }
    }

    suspend fun wait(ticks: Int) {
        suspend(TickCondition(ticks))
    }

    suspend fun wait(cond: () -> Boolean) {
        suspend(LambdaCondition(cond))
    }

    fun onCancel(action: () -> Unit) {
        cancelAction = action
    }

    fun cancel() = cancelAction.invoke()

    private suspend fun suspend(condition: RoutineCondition) {
        player.routines[type] = this
        return suspendCoroutineUninterceptedOrReturn { cont ->
            next = ConditionalContinuation(condition, cont)
            COROUTINE_SUSPENDED
        }
    }

    override fun compareTo(other: Routine) = type.compareTo(other.type)
}