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
package io.guthix.oldscape.server.net.state.game

import io.guthix.oldscape.server.event.GameEvent
import io.guthix.oldscape.server.net.IncPacket
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.player.Player

interface GamePacket : IncPacket {
    fun toEvent(world: World, player: Player) : GameEvent
}

data class GamePacketInDefinition(var size: Int, val decoder: GamePacketDecoder) {
    companion object {
        val inc = mutableMapOf<Int, GamePacketInDefinition>()
    }
}

data class GamePacketOutDefinition(val opcode: Int, val size: Int, val encoder: GamePacketEncoder) {
    companion object {
        val out = mutableMapOf<GamePacket, GamePacketOutDefinition>()
    }
}