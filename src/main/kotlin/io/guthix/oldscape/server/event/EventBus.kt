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

    private val eventListeners = mutableMapOf<KClass<out Event>, MutableList<ScriptScheduler<Event>>>()

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

    fun<E : Event> execute(event: E) {
        eventListeners[event::class]?.let {
            for(listener in it) listener.execute(event)
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
    fun <E : Event> register(type: KClass<E>, listener: ScriptScheduler<E>) {
        val listeners = eventListeners.getOrPut(type) {
            mutableListOf()
        }
        listeners.add(listener as ScriptScheduler<Event>)
    }
}

internal interface EventHolder {
    val events: MutableCollection<EventHandler<Event>>
}