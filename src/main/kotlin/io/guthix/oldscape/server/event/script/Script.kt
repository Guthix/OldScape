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
package io.guthix.oldscape.server.event.script

import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Player
import kotlin.reflect.KClass
import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript
abstract class Script {
    fun <E : InGameEvent> on(type: KClass<E>): ScriptFilter<E> = ScriptFilter(type)
}

class ScriptFilter<E : InGameEvent>(private val type: KClass<E>) {
    private var condition: EventHandler<E>.() -> Boolean = { true }

    fun where(condition: EventHandler<E>.() -> Boolean): ScriptFilter<E> {
        this.condition = condition
        return this
    }

    fun then(handler: EventHandler<E>.() -> Unit): ScriptScheduler<E> = ScriptScheduler(type, condition, handler)
}

class ScriptScheduler<in E : InGameEvent>(
    type: KClass<E>,
    private val condition: EventHandler<E>.() -> Boolean,
    private val script: EventHandler<E>.() -> Unit
) {
    init {
        EventBus.register(type, this)
    }

    fun schedule(event: E, player: Player, world: World) {
        val handler = EventHandler(event, player, world, script)
        if (handler.condition()) {
            player.inEvents.add(handler)
        }
    }
}

class EventHandler<out E : InGameEvent>(
    val event: E,
    val player: Player,
    val world: World,
    private val script: EventHandler<E>.() -> Unit
) {
    fun handle() {
        script()
    }
}

