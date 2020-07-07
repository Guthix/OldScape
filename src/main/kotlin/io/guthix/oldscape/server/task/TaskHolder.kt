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

import kotlin.coroutines.intrinsics.createCoroutineUnintercepted

interface TaskHolder {
    val tasks: MutableMap<TaskType, MutableList<Task>>

    fun addTask(type: TaskType, routine: suspend Task.() -> Unit): Task {
        val task = Task(type, this)
        task.next = ConditionalContinuation(TrueCondition, routine.createCoroutineUnintercepted(task, task))
        tasks.getOrPut(type) { mutableListOf() }.add(task)
        return task
    }

    fun cancelTasks(type: TaskType) {
        tasks[type]?.forEach(Task::cancel)
    }

    fun postProcess() {
        tasks.values.forEach { it.forEach(Task::postProcess) }
    }
}