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

import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.coroutines.resume
import kotlin.reflect.KClass

abstract class Script {
    fun <E : AssignedEvent> on(type: KClass<E>) = EventExecutor(type)
}

class EventExecutor<E : AssignedEvent>(val type: KClass<E>) {
    private var condition: E.() -> Boolean = { true }
    private var script: suspend E.() -> Unit = { }

    fun where(condition: E.() -> Boolean): EventExecutor<E> {
        this.condition = condition
        return this
    }

    fun then(script: suspend E.() -> Unit) {
        this.script = script
        EventRepository.register(type, this)
    }

    internal fun execute(event: E) {
        if(event.condition()) {
            event.next = ConditionalContinuation(TrueCondition(), script.createCoroutineUnintercepted(event, event))
            event.resume(Unit)
        }
    }
}