/**
 * This file is part of Guthix OldScape-Server.
 *
 * Guthix OldScape-Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.world.entity.interest

import io.guthix.oldscape.server.world.entity.Npc
import io.guthix.oldscape.server.world.entity.Player
import io.netty.buffer.ByteBuf

abstract class InterestUpdateType(val priority: Int, internal val mask: Int) : Comparable<InterestUpdateType> {
    override fun compareTo(other: InterestUpdateType): Int = when {
        priority < other.priority -> -1
        priority > other.priority -> 1
        else -> 0
    }
}

enum class MovementInterestUpdate { TELEPORT, RUN, WALK, STAY }

class PlayerUpdateType(
    priority: Int,
    mask: Int,
    val encode: ByteBuf.(Player) -> Unit
) : InterestUpdateType(priority, mask)

class NpcUpdateType(priority: Int, mask: Int, val encode: ByteBuf.(Npc) -> Unit) : InterestUpdateType(priority, mask)