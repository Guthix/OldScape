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

import io.guthix.buffer.readUnsignedByteADD
import io.guthix.buffer.readUnsignedByteNEG
import io.guthix.buffer.readUnsignedShortADD
import io.guthix.buffer.readUnsignedShortLEADD
import io.guthix.oldscape.server.dimensions.tiles
import io.guthix.oldscape.server.event.ObjectClickEvent
import io.guthix.oldscape.server.net.game.ClientEvent
import io.guthix.oldscape.server.net.game.FixedSize
import io.guthix.oldscape.server.net.game.GamePacketDecoder
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class Opobj1Packet : GamePacketDecoder(32, FixedSize(7)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val id = data.readUnsignedShortLEADD()
        val x = data.readUnsignedShortLEADD()
        val y = data.readUnsignedShortLE()
        val buttonPressed = data.readUnsignedByteNEG().toInt() == 1
        return ObjectClickEvent(id, x.tiles, y.tiles, buttonPressed, 1)
    }
}

class Opobj2Packet : GamePacketDecoder(80, FixedSize(7)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val y = data.readUnsignedShort()
        val x = data.readUnsignedShortADD()
        val buttonPressed = data.readUnsignedByteADD().toInt() == 1
        val id = data.readUnsignedShort()
        return ObjectClickEvent(id, x.tiles, y.tiles, buttonPressed, 2)
    }
}

class Opobj3Packet : GamePacketDecoder(68, FixedSize(7)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val buttonPressed = data.readUnsignedByte().toInt() == 1
        val x = data.readUnsignedShortLE()
        val y = data.readUnsignedShortADD()
        val id = data.readUnsignedShortLEADD()
        return ObjectClickEvent(id, x.tiles, y.tiles, buttonPressed, 3)
    }
}

class Opobj4Packet : GamePacketDecoder(20, FixedSize(7)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val buttonPressed = data.readUnsignedByte().toInt() == 1
        val id = data.readUnsignedShortLE()
        val x = data.readUnsignedShortLE()
        val y = data.readUnsignedShortADD()
        return ObjectClickEvent(id, x.tiles, y.tiles, buttonPressed, 4)
    }
}

class Opobj5Packet : GamePacketDecoder(28, FixedSize(7)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val id = data.readUnsignedShort()
        val y = data.readUnsignedShortADD()
        val buttonPressed = data.readUnsignedByteADD().toInt() == 1
        val x = data.readUnsignedShortADD()
        return ObjectClickEvent(id, x.tiles, y.tiles, buttonPressed, 5)
    }
}