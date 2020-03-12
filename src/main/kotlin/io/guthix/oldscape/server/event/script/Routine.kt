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
import io.guthix.oldscape.server.world.entity.character.player.Player
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.*

class Routine<E: InGameEvent>(
    private val type: Type,
    event: E,
    world: World,
    player: Player
) : Continuation<Unit>, EventHandler<E>(event, world, player) {
    var tickSuspended = false

    enum class Type { StrongAction, NormalAction, WeakAction }

    internal var cancelAction: EventHandler<E>.() -> Unit = {}

    internal var next: ConditionalContinuation? = null

    override val context: CoroutineContext = EmptyCoroutineContext

    override fun resumeWith(result: Result<Unit>) { }

    internal fun resumeIfPossible(): Boolean {
        next?.let {
            return if(!tickSuspended && it.canResume()) {
                player.routines.remove(type)
                it.continuation.resume(Unit)
                true
            } else false
        }
        return false
    }

    suspend fun wait(ticks: Int) {
        tickSuspended = true
        suspend(TickCondition(ticks))
    }

    suspend fun wait(cond: () -> Boolean) {
        suspend(LambdaCondition(cond))
    }

    fun onCancel(action: EventHandler<E>.() -> Unit) {
        cancelAction = action
    }

    fun cancel() = cancelAction.invoke(this)

    @Suppress("UNCHECKED_CAST")
    private suspend fun suspend(condition: RoutineCondition) {
        player.routines[type] = this as Routine<InGameEvent>
        return suspendCoroutineUninterceptedOrReturn { cont ->
            next = ConditionalContinuation(condition, cont)
            COROUTINE_SUSPENDED
        }
    }
}