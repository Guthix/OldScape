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
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class GamePacket(val opcode: Int, val type: PacketSize, val payload: ByteBuf) {
    enum class PacketSize { FIXED, VAR_BYTE, VAR_SHORT }
}

interface IncGamePacket : IncPacket {
    fun toEvent(world: World, player: Player) : GameEvent
}

interface OutGameEvent {
    fun encode(ctx: ChannelHandlerContext): GamePacket
}

data class GamePacketInDefinition(var size: Int, val decoder: GamePacketDecoder) {
    companion object {
        val inc = mutableMapOf<Int, GamePacketInDefinition>()
    }
}

interface GamePacketDecoder {
    fun decode(data: ByteBuf): IncGamePacket
}