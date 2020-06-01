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
package io.guthix.oldscape.server.net.game.inc

import io.guthix.buffer.*
import io.guthix.oldscape.server.event.PlayerClickClientEvent
import io.guthix.oldscape.server.net.game.ClientEvent
import io.guthix.oldscape.server.net.game.FixedSize
import io.guthix.oldscape.server.net.game.GamePacketDecoder
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class Opplayer1Packet : GamePacketDecoder(60, FixedSize(3)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val buttonPressed = data.readUnsignedByte().toInt() == 1
        val index = data.readUnsignedShortLEADD()
        return PlayerClickClientEvent(index, buttonPressed, 1)
    }
}

class Opplayer2Packet : GamePacketDecoder(25, FixedSize(3)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val index = data.readUnsignedShortADD()
        val buttonPressed = data.readUnsignedByteADD().toInt() == 1
        return PlayerClickClientEvent(index, buttonPressed, 2)
    }
}

class Opplayer3Packet : GamePacketDecoder(59, FixedSize(3)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val buttonPressed = data.readUnsignedByteADD().toInt() == 1
        val index = data.readUnsignedShort()
        return PlayerClickClientEvent(index, buttonPressed, 3)
    }
}

class Opplayer4Packet : GamePacketDecoder(75, FixedSize(3)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val index = data.readUnsignedShort()
        val buttonPressed = data.readUnsignedByteSUB().toInt() == 1
        return PlayerClickClientEvent(index, buttonPressed, 4)
    }
}

class Opplayer5Packet : GamePacketDecoder(51, FixedSize(3)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val buttonPressed = data.readUnsignedByteADD().toInt() == 1
        val index = data.readUnsignedShort()
        return PlayerClickClientEvent(index, buttonPressed, 5)
    }
}

class Opplayer6Packet : GamePacketDecoder(43, FixedSize(3)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val buttonPressed = data.readUnsignedByteNEG().toInt() == 1
        val index = data.readUnsignedShortLEADD()
        return PlayerClickClientEvent(index, buttonPressed, 6)
    }
}

class Opplayer7Packet : GamePacketDecoder(94, FixedSize(3)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val buttonPressed = data.readUnsignedByte().toInt() == 1
        val index = data.readUnsignedShort()
        return PlayerClickClientEvent(index, buttonPressed, 7)
    }
}

class Opplayer8Packet : GamePacketDecoder(40, FixedSize(3)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val buttonPressed = data.readUnsignedByte().toInt() == 1
        val index = data.readUnsignedShort()
        return PlayerClickClientEvent(index, buttonPressed, 8)
    }
}