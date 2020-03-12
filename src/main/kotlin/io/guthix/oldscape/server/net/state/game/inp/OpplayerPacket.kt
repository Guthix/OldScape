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
package io.guthix.oldscape.server.net.state.game.inp

import io.guthix.buffer.*
import io.guthix.oldscape.server.net.state.game.ClientEvent
import io.guthix.oldscape.server.event.PlayerClickClientEvent
import io.guthix.oldscape.server.net.state.game.FixedSize
import io.guthix.oldscape.server.net.state.game.GamePacketDecoder
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class Opplayer1Packet : GamePacketDecoder(14, FixedSize(3)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val playerIndex = data.readUnsignedShort()
        val buttonPressed = data.readUnsignedByte().toInt() == 1
        return PlayerClickClientEvent(playerIndex, buttonPressed, 1)
    }
}

class Opplayer2Packet : GamePacketDecoder(78, FixedSize(3)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val buttonPressed = data.readUnsignedByteSUB().toInt() == 1
        val playerIndex = data.readUnsignedShortLE()
        return PlayerClickClientEvent(playerIndex, buttonPressed, 2)
    }
}

class Opplayer3Packet : GamePacketDecoder(46, FixedSize(3)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val buttonPressed = data.readUnsignedByteADD().toInt() == 1
        val playerIndex = data.readUnsignedShortLEADD()
        return PlayerClickClientEvent(playerIndex, buttonPressed, 3)
    }
}

class Opplayer4Packet : GamePacketDecoder(50, FixedSize(3)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val playerIndex = data.readUnsignedShortLEADD()
        val buttonPressed = data.readUnsignedByteADD().toInt() == 1
        return PlayerClickClientEvent(playerIndex, buttonPressed, 4)
    }
}

class Opplayer5Packet : GamePacketDecoder(24, FixedSize(3)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val buttonPressed = data.readUnsignedByteSUB().toInt() == 1
        val playerIndex = data.readUnsignedShortADD()
        return PlayerClickClientEvent(playerIndex, buttonPressed, 5)
    }
}

class Opplayer6Packet : GamePacketDecoder(57, FixedSize(3)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val buttonPressed = data.readUnsignedByteNEG().toInt() == 1
        val playerIndex = data.readUnsignedShort()
        return PlayerClickClientEvent(playerIndex, buttonPressed, 6)
    }
}

class Opplayer7Packet : GamePacketDecoder(72, FixedSize(3)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val playerIndex = data.readUnsignedShortLE()
        val buttonPressed = data.readUnsignedByte().toInt() == 1
        return PlayerClickClientEvent(playerIndex, buttonPressed, 7)
    }
}

class Opplayer8Packet : GamePacketDecoder(56, FixedSize(3)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val playerIndex = data.readUnsignedShortLEADD()
        val buttonPressed = data.readUnsignedByteNEG().toInt() == 1
        return PlayerClickClientEvent(playerIndex, buttonPressed, 8)
    }
}