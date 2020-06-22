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
package io.guthix.oldscape.server.plugin

import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.event.EventHolder
import io.guthix.oldscape.server.event.GameEvent
import kotlin.reflect.KClass
import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript
abstract class Script {
    fun <E : GameEvent> on(type: KClass<E>): ScriptFilter<E> = ScriptFilter(type)
}

class ScriptFilter<E : GameEvent>(private val type: KClass<E>) {
    private var condition: E.() -> Boolean = { true }

    fun where(condition: E.() -> Boolean): ScriptFilter<E> {
        this.condition = condition
        return this
    }

    fun then(plugin: E.() -> Unit): ScriptScheduler<E> = ScriptScheduler(type, condition, plugin)
}

class ScriptScheduler<in E : GameEvent>(
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
}

class EventHandler<out E : GameEvent>(
    val event: E,
    private val plugin: E.() -> Unit
) {
    fun handle() {
        event.plugin()
    }
}

