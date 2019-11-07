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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.net.state.game

import io.guthix.oldscape.server.GameEvent
import io.guthix.oldscape.server.net.state.IsaacRandom
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import kotlinx.io.IOException

class GameEncoder(private val encodeCipher: IsaacRandom) : MessageToByteEncoder<PacketEvent>() {
    override fun encode(ctx: ChannelHandlerContext, msg: PacketEvent, out: ByteBuf) {
        val packet = GamePacketOutDefinition.out[msg] ?: throw IOException(
            "No packet definition found for $msg"
        )
        val payload = packet.encoder.encode(msg)
        out.writeByte(packet.opcode + encodeCipher.nextInt())
        when(packet.size) {
            -2 -> out.writeShort(payload.readableBytes())
            -1 -> out.writeByte(payload.readableBytes())
        }
        out.writeBytes(payload)
    }
}