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
package io.guthix.oldscape.server.net.game.inc

import io.guthix.buffer.readUnsignedSmallSmart
import io.guthix.oldscape.server.api.Huffman
import io.guthix.oldscape.server.event.PlayerGameEvent
import io.guthix.oldscape.server.event.PublicMessageEvent
import io.guthix.oldscape.server.net.game.GamePacketDecoder
import io.guthix.oldscape.server.net.game.VarByteSize
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Player
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class MessagePublicPacket : GamePacketDecoder(22, VarByteSize) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        buf.readUnsignedByte()
        val color = buf.readUnsignedByte().toInt()
        val effect = buf.readUnsignedByte().toInt()
        val len = buf.readUnsignedSmallSmart()
        val compr = ByteArray(buf.readableBytes()).apply { buf.readBytes(this) }
        val msg = String(Huffman.decompress(compr, len))
        return PublicMessageEvent(color, effect, msg, player, world)
    }
}