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
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.reflect.KClass

@KotlinScript
abstract class Script {
    fun <E: InGameEvent>on(type: KClass<E>) = ScriptFilter(type)
}

class ScriptFilter<E: InGameEvent>(private val type: KClass<E>) {
    private var condition: EventHandler<E>.() -> Boolean = { true }

    fun where(condition: EventHandler<E>.() -> Boolean): ScriptFilter<E> {
        this.condition = condition
        return this
    }

    fun then(handler: EventHandler<E>.() -> Unit) = DefaultScriptScheduler(type, condition, handler)

    fun then(
        routineType: Routine.Type,
        replace: Boolean = false,
        handler: suspend SuspendableEventHandler<E>.() -> Unit
    ) = SuspendableScriptScheduler(type, condition, routineType, replace, handler)
}

abstract class ScriptScheduler<E : InGameEvent>(
    protected val type: KClass<E>,
    protected val condition: EventHandler<E>.() -> Boolean
) {
    abstract fun schedule(event: E, player: Player, world: World)
}

class DefaultScriptScheduler<E: InGameEvent>(
    type: KClass<E>,
    condition: EventHandler<E>.() -> Boolean,
    private val script: EventHandler<E>.() -> Unit
) : ScriptScheduler<E>(type, condition) {
    init {
        EventBus.register(type, this)
    }

    override fun schedule(event: E, player: Player, world: World) {
        val handler = DefaultEventHandler(Routine.Type.Event, event, player, world, script)
        if (handler.condition()) { player.inEvents.add(handler) }
    }
}

class SuspendableScriptScheduler<E : InGameEvent>(
    type: KClass<E>,
    condition: EventHandler<E>.() -> Boolean,
    private val routineType: Routine.Type,
    private val replace: Boolean = false,
    private val script: suspend SuspendableEventHandler<E>.() -> Unit
) : ScriptScheduler<E>(type, condition) {
    init {
        EventBus.register(type, this)
    }

    private var onCancel: suspend SuspendableEventHandler<E>.() -> Unit = {}

    fun onCancel(routine: suspend SuspendableEventHandler<E>.() -> Unit) {
        onCancel = routine
    }

    @Suppress("UNCHECKED_CAST")
    override fun schedule(event: E, player: Player, world: World) {
        val routine = SuspendableEventHandler(routineType, event, player, world)
        if (routine.condition()) {
            routine.next = ConditionalContinuation(TrueCondition, script.createCoroutineUnintercepted(routine, routine))
            routine.onCancel(onCancel)
            if(replace) {
                val routines = player.routines.remove(routineType)
                routines?.forEach { it.cancel() }
                player.routines[routineType] = mutableListOf<Routine>(routine)
            } else {
                player.routines.getOrPut(routineType) { mutableListOf() }.add(routine)
            }
            routine.run()
        }
    }
}