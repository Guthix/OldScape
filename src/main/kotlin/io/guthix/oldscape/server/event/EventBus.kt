/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.event

import io.github.classgraph.ClassGraph
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.character.player.Player
import mu.KotlinLogging
import kotlin.reflect.KClass

private val logger = KotlinLogging.logger { }

object EventBus {
    const val pkg = "io.guthix.oldscape.server"

    private val eventListeners = mutableMapOf<KClass<out GameEvent>, MutableList<EventListener<in GameEvent>>>()

    fun loadScripts() {
        ClassGraph().whitelistPackages(pkg).scan().use { scanResult ->
            val pluginClassList = scanResult
                .getSubclasses("io.guthix.oldscape.server.event.Script")
                .directOnly()
            pluginClassList.forEach {
                it.loadClass(Script::class.java).getDeclaredConstructor().newInstance()
            }
            logger.info { "Loaded ${pluginClassList.size} scripts" }
        }
    }

    fun <E : GameEvent> schedule(event: E, world: World, player: Player) = eventListeners[event::class]?.let {
        for (listener in it) {
            listener.schedule(event, world, player)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <E : GameEvent> register(type: KClass<E>, listener: EventListener<E>) {
        val listeners = eventListeners.getOrPut(type) {
            mutableListOf()
        }
        listeners.add(listener as EventListener<GameEvent>)
    }
}