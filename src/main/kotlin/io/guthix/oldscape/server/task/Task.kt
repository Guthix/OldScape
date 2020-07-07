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
package io.guthix.oldscape.server.task

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume

class Task(val type: TaskType, private val holder: TaskHolder) : Continuation<Unit> {
    internal var next: ConditionalContinuation? = null

    private var cancelation: ConditionalContinuation? = null

    override val context: CoroutineContext = EmptyCoroutineContext

    fun run(): Boolean {
        return next?.let {
            if (it.canResume()) {
                holder.tasks[type]?.remove(this)
                it.continuation.resume(Unit)
                true
            } else false
        } ?: run {
            holder.tasks[type]?.remove(this)
            false
        }
    }

    fun cancel() {
        next = cancelation
    }

    fun onCancel(action: suspend Task.() -> Unit) {
        cancelation = ConditionalContinuation(TrueCondition, action.createCoroutineUnintercepted(this, this))
    }

    fun postProcess() {
        next?.postProcess()
    }

    override fun resumeWith(result: Result<Unit>) {}

    suspend fun wait(ticks: Int) {
        suspend(TickCondition(ticks))
    }

    suspend fun wait(cond: () -> Boolean) {
        suspend(LambdaCondition(cond))
    }

    private suspend fun suspend(condition: TaskWaitCondition) {
        holder.tasks.getOrPut(type) { mutableListOf() }.add(this)
        return suspendCoroutineUninterceptedOrReturn { cont ->
            next = ConditionalContinuation(condition, cont)
            COROUTINE_SUSPENDED
        }
    }
}

interface TaskType

object StrongTask : TaskType

object NormalTask : TaskType

object WeakTask : TaskType
