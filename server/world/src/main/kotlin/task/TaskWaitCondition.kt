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

interface TaskWaitCondition {
    fun canResume(): Boolean

    fun postProcess() {}
}

class TickCondition(var tickCount: Int) : TaskWaitCondition {
    var ticked: Boolean = false

    override fun canResume(): Boolean = if (!ticked) {
        ticked = true
        tickCount-- <= 0
    } else false

    override fun postProcess() {
        ticked = false
    }
}

class LambdaCondition(private val cond: () -> Boolean) : TaskWaitCondition {
    override fun canResume(): Boolean = cond.invoke()
}

object TrueCondition : TaskWaitCondition {
    override fun canResume(): Boolean = true
}

class ConditionalContinuation(
    val condition: TaskWaitCondition,
    val continuation: Continuation<Unit>
) : TaskWaitCondition by condition