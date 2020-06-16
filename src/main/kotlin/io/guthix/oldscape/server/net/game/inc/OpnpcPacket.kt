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

import io.guthix.buffer.readUnsignedByteNEG
import io.guthix.buffer.readUnsignedByteSUB
import io.guthix.buffer.readUnsignedShortADD
import io.guthix.buffer.readUnsignedShortLEADD
import io.guthix.oldscape.server.event.NpcClickClientEvent
import io.guthix.oldscape.server.event.NpcExamineClientEvent
import io.guthix.oldscape.server.net.game.ClientEvent
import io.guthix.oldscape.server.net.game.FixedSize
import io.guthix.oldscape.server.net.game.GamePacketDecoder
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class Opnpc1Packet : GamePacketDecoder(56, FixedSize(3)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val index = data.readUnsignedShortLE()
        val pressed = data.readUnsignedByteNEG().toInt() == 1
        return NpcClickClientEvent(index, pressed, 1)
    }
}

class Opnpc2Packet : GamePacketDecoder(4, FixedSize(3)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val index = data.readUnsignedShort()
        val pressed = data.readUnsignedByteNEG().toInt() == 1
        return NpcClickClientEvent(index, pressed, 2)
    }
}

class Opnpc3Packet : GamePacketDecoder(46, FixedSize(3)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val index = data.readUnsignedShortADD()
        val pressed = data.readUnsignedByteNEG().toInt() == 1
        return NpcClickClientEvent(index, pressed, 3)
    }
}

class Opnpc4Packet : GamePacketDecoder(12, FixedSize(3)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val pressed = data.readUnsignedByteSUB().toInt() == 1
        val index = data.readUnsignedShortLE()
        return NpcClickClientEvent(index, pressed, 4)
    }
}

class Opnpc5Packet : GamePacketDecoder(37, FixedSize(3)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val pressed = data.readUnsignedByte().toInt() == 1
        val index = data.readUnsignedShort()
        return NpcClickClientEvent(index, pressed, 5)
    }
}

class Opnpc6Packet : GamePacketDecoder(91, FixedSize(2)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val id = data.readUnsignedShortLEADD()
        return NpcExamineClientEvent(id)
    }
}


