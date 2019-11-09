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

import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.player.Player
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript
abstract class Script {
    fun <E: GameEvent>on(event: E) = EventExecutor(event)
}

class EventExecutor<E: GameEvent>(val event: E) {
    private var script: suspend ScriptCoroutine<E>.() -> Unit = { }

    fun then(script: suspend ScriptCoroutine<E>.() -> Unit): EventExecutor<E> {
        this.script = script
        return this
    }

    fun register(eventBus: EventBus) {
        eventBus.register(event, this)
    }

    internal fun execute(world: World, player: Player) {
        val coroutine = ScriptCoroutine<E>(event, world, player)
        script.startCoroutineUninterceptedOrReturn(coroutine, coroutine)
    }
}