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
import io.guthix.oldscape.server.dimensions.tiles
import io.guthix.oldscape.server.net.state.game.ClientEvent
import io.guthix.oldscape.server.event.ObjectClickEvent
import io.guthix.oldscape.server.net.state.game.FixedSize
import io.guthix.oldscape.server.net.state.game.GamePacketDecoder
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class Opobj1Packet : GamePacketDecoder(45, FixedSize(7)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val buttonPressed = data.readUnsignedByteNEG().toInt() == 1
        val x = data.readUnsignedShortLEADD()
        val id = data.readUnsignedShortADD()
        val y = data.readUnsignedShortLEADD()
        return ObjectClickEvent(id, x.tiles, y.tiles, buttonPressed, 1)
    }
}

class Opobj2Packet : GamePacketDecoder(90, FixedSize(7)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val id = data.readUnsignedShortADD()
        val x = data.readUnsignedShortADD()
        val y = data.readUnsignedShort()
        val buttonPressed = data.readUnsignedByte().toInt() == 1
        return ObjectClickEvent(id, x.tiles, y.tiles, buttonPressed, 2)
    }
}

class Opobj3Packet : GamePacketDecoder(65, FixedSize(7)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val id = data.readUnsignedShort()
        val buttonPressed = data.readUnsignedByteSUB().toInt() == 1
        val y = data.readUnsignedShort()
        val x = data.readUnsignedShortLE()
        return ObjectClickEvent(id, x.tiles, y.tiles, buttonPressed, 3)
    }
}

class Opobj4Packet : GamePacketDecoder(29, FixedSize(7)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val buttonPressed = data.readUnsignedByte().toInt() == 1
        val id = data.readUnsignedShortADD()
        val x = data.readUnsignedShortADD()
        val y = data.readUnsignedShortADD()
        return ObjectClickEvent(id, x.tiles, y.tiles, buttonPressed, 4)
    }
}

class Opobj5Packet : GamePacketDecoder(30, FixedSize(7)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val x = data.readUnsignedShortADD()
        val buttonPressed = data.readUnsignedByte().toInt() == 1
        val y = data.readUnsignedShortLE()
        val id = data.readUnsignedShortLE()
        return ObjectClickEvent(id, x.tiles, y.tiles, buttonPressed, 5)
    }
}