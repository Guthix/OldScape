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
package io.guthix.oldscape.server.api

import io.guthix.oldscape.server.routine.ConditionalContinuation
import io.guthix.oldscape.server.routine.InitialCondition
import io.guthix.oldscape.server.routine.Routine
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.character.player.Player
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.reflect.KClass
import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript
abstract class Script {
    fun <E: GameEvent>on(type: KClass<E>) = ScriptFilter(type)
}

class ScriptFilter<E: GameEvent>(private val type: KClass<E>) {
    private var condition: EventHandler<E>.() -> Boolean = { true }

    fun where(condition: EventHandler<E>.() -> Boolean): ScriptFilter<E> {
        this.condition = condition
        return this
    }

    fun then(script: EventHandler<E>.() -> Unit) = DefaultScriptScheduler(type, condition, script)

    fun then(routineType: Routine.Type, script: suspend Routine<E>.() -> Unit) = RoutineScriptScheduler(
        type, condition, routineType, script
    )
}

abstract class ScriptScheduler<E : GameEvent>(
    protected val type: KClass<E>,
    val condition: EventHandler<E>.() -> Boolean
) {
    abstract fun schedule(event: E, world: World, player: Player)
}

class DefaultScriptScheduler<E: GameEvent>(
    type: KClass<E>,
    condition: EventHandler<E>.() -> Boolean,
    val script: EventHandler<E>.() -> Unit
) : ScriptScheduler<E>(type, condition) {
    init {
        EventBus.register(type, this)
    }

    override fun schedule(event: E, world: World, player: Player) {
        val handler = EventHandler(event, world, player)
        if (handler.condition()) {
            player.inEvents.add { handler.script() }
        }
    }
}

class RoutineScriptScheduler<E : GameEvent>(
    type: KClass<E>,
    condition: EventHandler<E>.() -> Boolean,
    val routineType: Routine.Type,
    val script: suspend Routine<E>.() -> Unit
) : ScriptScheduler<E>(type, condition) {
    init {
        EventBus.register(type, this)
    }

    var onCancel: EventHandler<E>.() -> Unit = {}

    fun onCancel(routine: EventHandler<E>.() -> Unit) {
        onCancel = routine
    }

    @Suppress("UNCHECKED_CAST")
    override fun schedule(event: E, world: World, player: Player) {
        val routine = Routine(routineType, event, world, player)
        if (routine.condition()) {
            routine.next = ConditionalContinuation(
                InitialCondition, script.createCoroutineUnintercepted(routine, routine)
            )
            routine.onCancel(onCancel)
            player.routines[routineType]?.cancel()
            player.routines[routineType] = routine as Routine<GameEvent>
            routine.resumeIfPossible()
        }
    }
}

open class EventHandler<E : GameEvent>(val event: E, val world: World, val player: Player)