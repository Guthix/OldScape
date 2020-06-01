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
import io.guthix.oldscape.server.dimensions.tiles
import io.guthix.oldscape.server.event.LocationClickEvent
import io.guthix.oldscape.server.net.game.ClientEvent
import io.guthix.oldscape.server.net.game.FixedSize
import io.guthix.oldscape.server.net.game.GamePacketDecoder
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class Oploc1Packet : GamePacketDecoder(76, FixedSize(7)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val pressed = data.readUnsignedByteSUB().toInt() == 1
        val x = data.readUnsignedShortLEADD()
        val id = data.readUnsignedShortLE()
        val y = data.readUnsignedShortLE()
        return LocationClickEvent(x.tiles, y.tiles, id, pressed)
    }
}

class Oploc2Packet : GamePacketDecoder(36, FixedSize(7)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val id = data.readUnsignedShort()
        val x = data.readUnsignedShortLEADD()
        val y = data.readUnsignedShortLEADD()
        val pressed = data.readUnsignedByteNEG().toInt() == 1
        return LocationClickEvent(x.tiles, y.tiles, id, pressed)
    }
}

class Oploc3Packet : GamePacketDecoder(89, FixedSize(7)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val x = data.readUnsignedShortADD()
        val y = data.readUnsignedShortLEADD()
        val pressed = data.readUnsignedByteADD().toInt() == 1
        val id = data.readUnsignedShortADD()
        return LocationClickEvent(x.tiles, y.tiles, id, pressed)
    }
}

class Oploc4Packet : GamePacketDecoder(81, FixedSize(7)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val id = data.readUnsignedShortADD()
        val pressed = data.readUnsignedByte().toInt() == 1
        val x = data.readUnsignedShort()
        val y = data.readUnsignedShortLE()
        return LocationClickEvent(x.tiles, y.tiles, id, pressed)
    }
}

class Oploc5Packet : GamePacketDecoder(67, FixedSize(7)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val id = data.readUnsignedShortLEADD()
        val x = data.readUnsignedShortLE()
        val pressed = data.readUnsignedByte().toInt() == 1
        val y = data.readUnsignedShort()
        return LocationClickEvent(x.tiles, y.tiles, id, pressed)
    }
}