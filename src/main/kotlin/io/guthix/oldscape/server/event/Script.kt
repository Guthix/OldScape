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
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.reflect.KClass
import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript
abstract class Script {
    fun <E: GameEvent>on(type: KClass<E>) = EventListener(type)
}

class EventListener<E: GameEvent>(private val type: KClass<E>) {
    internal var condition: GameRoutine<E>.() -> Boolean = { true }

    private var script: suspend GameRoutine<E>.() -> Unit = { }

    fun where(condition: GameRoutine<E>.() -> Boolean): EventListener<E> {
        this.condition = condition
        return this
    }

    fun then(script: suspend GameRoutine<E>.() -> Unit) {
        this.script = script
        registerListener()
    }

    private fun registerListener() {
        EventBus.register(type, this)
    }

    fun schedule(event: E, world: World, player: Player) {
        val routine = GameRoutine(world, player, event)
        if(routine.condition()) {
            routine.next = ConditionalContinuation(InitialCondition, script.createCoroutineUnintercepted(routine, routine))
            routine.player.continuations.add(routine)
        }
    }
}