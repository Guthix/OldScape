/*
 * Copyright 2018-2021 Guthix
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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