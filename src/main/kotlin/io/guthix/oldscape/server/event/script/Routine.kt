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

import io.guthix.oldscape.server.world.entity.Player
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.coroutines.*

abstract class Routine(protected val type: Type, protected open val player: Player) {
    enum class Type { Event, Strong, Normal, Weak, Chat, Background }

    internal abstract fun run(): Boolean

    internal abstract fun cancel()

    internal abstract fun postProcess()
}

open class SuspendableRoutine(
    type: Type,
    player: Player
) : Routine(type, player), Continuation<Unit> {
    private var tickSuspended = false

    internal var next: ConditionalContinuation? = null

    private var cancelation: ConditionalContinuation? = null

    override val context: CoroutineContext = EmptyCoroutineContext

    override fun run(): Boolean {
        return next?.let {
            if(!tickSuspended && it.canResume()) {
                player.routines.remove(type)
                it.continuation.resume(Unit)
                true
            } else false
        } ?: false
    }

    override fun cancel() { next = cancelation }

    override fun postProcess() { tickSuspended = false }

    fun <E : Routine>onCancel(action: suspend E.() -> Unit) {
        @Suppress("UNCHECKED_CAST")
        cancelation = ConditionalContinuation(TrueCondition, action.createCoroutineUnintercepted(this as E, this))
    }

    override fun resumeWith(result: Result<Unit>) { }

    suspend fun wait(ticks: Int) {
        tickSuspended = true
        suspend(TickCondition(ticks))
    }

    suspend fun wait(cond: () -> Boolean) {
        suspend(LambdaCondition(cond))
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun suspend(condition: SuspensionCondition) {
        player.routines.getOrPut(type) { mutableListOf() }.add(this)
        return suspendCoroutineUninterceptedOrReturn { cont ->
            next = ConditionalContinuation(condition, cont)
            COROUTINE_SUSPENDED
        }
    }
}