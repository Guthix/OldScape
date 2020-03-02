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

import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.character.player.Player
import kotlin.reflect.KClass
import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript
abstract class Script {
    fun <E: GameEvent>on(type: KClass<E>) = EventListener(type)
}

class EventHandler<E : GameEvent>(val event: E, val world: World, val player: Player)

class EventListener<E: GameEvent>(private val type: KClass<E>) {
    internal var condition: EventHandler<E>.() -> Boolean = { true }

    private var script: EventHandler<E>.() -> Unit = { }

    fun where(condition: EventHandler<E>.() -> Boolean): EventListener<E> {
        this.condition = condition
        return this
    }

    fun then(script: EventHandler<E>.() -> Unit) {
        this.script = script
        registerListener()
    }

    private fun registerListener() {
        EventBus.register(type, this)
    }

    @Suppress("UNCHECKED_CAST")
    fun schedule(event: E, world: World, player: Player) {
        val routine = EventHandler(event, world, player)
        if(routine.condition()) {
            player.inEvents.add { routine.script() }
        }
    }
}