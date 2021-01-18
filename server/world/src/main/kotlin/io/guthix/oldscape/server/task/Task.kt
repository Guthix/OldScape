/*
 * Copyright 2018-2021 Guthix
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

    private var finalization: ConditionalContinuation? = null

    override val context: CoroutineContext = EmptyCoroutineContext

    fun run(): Boolean {
        return next?.let {
            if (it.canResume()) {
                holder.tasks[type]?.remove(this)
                it.continuation.resume(Unit)
                true
            } else false
        } ?: run {
            finalization?.continuation?.resume(Unit)
            holder.tasks[type]?.remove(this)
            false
        }
    }

    /** Similar to [TaskHolder.cancelTasks] but also cancels the currently executed task. */
    fun cancelTasks(type: TaskType) {
        holder.tasks[type]?.forEach(Task::cancel)
        cancel()
    }

    fun cancel() {
        holder.tasks.getOrPut(type) { mutableSetOf() }.add(this)
        next = null
    }

    fun finalize(action: suspend Task.() -> Unit) {
        finalization = ConditionalContinuation(TrueCondition, action.createCoroutineUnintercepted(this, this))
    }

    fun postProcess() {
        next?.postProcess()
    }

    override fun resumeWith(result: Result<Unit>) {
        result.exceptionOrNull()?.let {
            it.printStackTrace()
            cancel()
        }
    }

    suspend fun wait(ticks: Int) {
        suspend(TickCondition(ticks))
    }

    suspend fun wait(cond: () -> Boolean) {
        suspend(LambdaCondition(cond))
    }

    private suspend fun suspend(condition: TaskWaitCondition) {
        holder.tasks.getOrPut(type) { mutableSetOf() }.add(this)
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
