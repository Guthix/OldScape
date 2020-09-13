/*
 * Copyright 2018-2020 Guthix
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
package io.guthix.oldscape.server.plugin

import io.guthix.oldscape.server.event.Event
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.event.EventHolder
import kotlin.reflect.KClass
import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript
abstract class Script {
    fun <E : Event> on(type: KClass<E>): ScriptFilter<E> = ScriptFilter(type)
}

class ScriptFilter<E : Event>(private val type: KClass<E>) {
    private var condition: E.() -> Boolean = { true }

    fun where(condition: E.() -> Boolean): ScriptFilter<E> {
        this.condition = condition
        return this
    }

    fun then(plugin: E.() -> Unit): ScriptScheduler<E> = ScriptScheduler(type, condition, plugin)
}

class ScriptScheduler<in E : Event>(
    type: KClass<E>,
    private val condition: E.() -> Boolean,
    private val plugin: E.() -> Unit
) {
    init {
        EventBus.register(type, this)
    }

    internal fun schedule(event: E, holder: EventHolder) {
        val handler = EventHandler(event, plugin)
        if (event.condition()) {
            holder.events.add(handler)
        }
    }

    internal fun execute(event: E) {
        val handler = EventHandler(event, plugin)
        handler.handle()
    }
}

class EventHandler<out E : Event>(
    val event: E,
    private val plugin: E.() -> Unit
) {
    fun handle() {
        event.plugin()
    }
}

