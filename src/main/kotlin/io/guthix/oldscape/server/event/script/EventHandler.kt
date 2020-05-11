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

interface EventHandler<E : InGameEvent> {
    val event: E
    val player: Player
    val world: World
}

class DefaultEventHandler<E : InGameEvent>(
    type: Type,
    override val event: E,
    override val player: Player,
    override val world: World,
    private val routine: EventHandler<E>.() -> Unit
) : Routine(type, player), EventHandler<E> {
    override fun run(): Boolean {
        routine()
        return false
    }

    override fun cancel() { }

    override fun postProcess() { }
}

class SuspendableEventHandler<E : InGameEvent>(
    type: Type,
    override val event: E,
    override val player: Player,
    override val world: World
) : SuspendableRoutine(type, player), EventHandler<E>