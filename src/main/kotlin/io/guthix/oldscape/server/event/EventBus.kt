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

import kotlin.reflect.KClass

class EventBus {
    private val eventListeners = mutableMapOf<KClass<out AssignedGameEvent>, MutableList<EventListener<in AssignedGameEvent>>>()

    fun <E : AssignedGameEvent> notify(event: E) = eventListeners[event::class]?.let {
        for (listener in it) {
            listener.execute(event)
        }
    }

    fun <E : AssignedGameEvent> register(type: KClass<E>, listener: EventListener<E>) {
        val listeners = eventListeners.getOrPut(type) {
            mutableListOf()
        }
        listeners.add(listener as EventListener<AssignedGameEvent>)
    }
}