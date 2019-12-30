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

import io.guthix.oldscape.server.world.entity.character.player.Player
import kotlin.coroutines.*
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn

open class Action(val type: Type, val player: Player) : Continuation<Unit>, Comparable<Action> {
    enum class Type(val priority: Int) { STRONG(1), NORMAL(2), WEAK(3) }

    internal var next: ConditionalContinuation? = null

    override val context: CoroutineContext = EmptyCoroutineContext

    override fun resumeWith(result: Result<Unit>) { }

    internal fun resumeIfPossible()  = next?.let {
        if(it.canResume()) {
            player.actions.remove(this)
            it.continuation.resume(Unit)
        }
    }

    suspend fun wait(ticks: Int) {
        suspend(TickCondition(ticks))
    }

    suspend fun wait(cond: () -> Boolean) {
        suspend(LambdaCondition(cond))
    }

    private suspend fun suspend(condition: ActionCondition) {
        player.actions.add(this)
        return suspendCoroutineUninterceptedOrReturn { cont ->
            next = ConditionalContinuation(condition, cont)
            COROUTINE_SUSPENDED
        }
    }

    override fun compareTo(other: Action) = when {
        type.priority < other.type.priority -> -1
        type.priority > other.type.priority -> 1
        else -> 0
    }
}