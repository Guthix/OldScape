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
package io.guthix.oldscape.server.event

import io.guthix.oldscape.server.world.entity.player.Player
import kotlin.coroutines.*
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn

open class ScriptCoroutine(val player: Player) : Continuation<Unit> {
    private var next: ConditionalContinuation? = null

    override val context: CoroutineContext = EmptyCoroutineContext

    override fun resumeWith(result: Result<Unit>) {
        next?.let {
            if(result.isSuccess) {
                it.continuation.resume(Unit)
            } else {
                player.continuations.remove(this)
                result.getOrThrow()
            }
        }
    }

    internal fun checkResume()  = next?.let {
        if(it.canResume()) {
            player.continuations.remove(this)
            resumeWith(Result.success(Unit))
        }
    }

    protected suspend fun wait(ticks: Int) {
        suspend(TickCondition(ticks))
    }

    protected suspend fun wait(cond: () -> Boolean) {
        suspend(LambdaCondition(cond))
    }

    private suspend fun suspend(condition: Condition) {
        player.continuations.add(this)
        return suspendCoroutineUninterceptedOrReturn { cont ->
            next = ConditionalContinuation(condition, cont)
            COROUTINE_SUSPENDED
        }
    }
}