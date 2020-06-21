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
package io.guthix.oldscape.server.event

import io.github.classgraph.ClassGraph
import io.guthix.oldscape.server.plugin.EventHandler
import io.guthix.oldscape.server.plugin.Script
import io.guthix.oldscape.server.plugin.ScriptScheduler
import mu.KotlinLogging
import kotlin.reflect.KClass

private val logger = KotlinLogging.logger { }

object EventBus {
    const val pkg: String = "io.guthix.oldscape.server"

    private val eventListeners = mutableMapOf<KClass<out GameEvent>, MutableList<ScriptScheduler<GameEvent>>>()

    fun loadScripts() {
        ClassGraph().whitelistPackages(pkg).scan().use { scanResult ->
            val pluginClassList = scanResult
                .getSubclasses("io.guthix.oldscape.server.plugin.Script")
                .directOnly()
            pluginClassList.forEach {
                it.loadClass(Script::class.java).getDeclaredConstructor().newInstance()
            }
            logger.info { "Loaded ${pluginClassList.size} scripts" }
        }
    }

    fun <E : GameEvent> schedule(event: E) {
        eventListeners[event::class]?.let {
            for (listener in it) listener.schedule(event, event.world)
        }
    }

    fun <E : PlayerGameEvent> schedule(event: E) {
        eventListeners[event::class]?.let {
            for (listener in it) listener.schedule(event, event.player)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <E : GameEvent> register(type: KClass<E>, listener: ScriptScheduler<E>) {
        val listeners = eventListeners.getOrPut(type) {
            mutableListOf()
        }
        listeners.add(listener as ScriptScheduler<GameEvent>)
    }
}

internal interface EventHolder {
    val events: MutableCollection<EventHandler<GameEvent>>
}