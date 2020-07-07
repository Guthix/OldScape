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