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
import io.guthix.oldscape.server.event.GameEvent
import io.guthix.oldscape.server.event.imp.LocationClickEvent
import io.guthix.oldscape.server.net.state.game.FixedSize
import io.guthix.oldscape.server.net.state.game.GamePacketDecoder
import io.guthix.oldscape.server.world.mapsquare.zone.tile.tiles
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class Oploc1Packet : GamePacketDecoder(79, FixedSize(7)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): GameEvent {
        val x = data.readUnsignedShortLEADD()
        val y = data.readUnsignedShortLEADD()
        val id = data.readUnsignedShortLE()
        val pressed = data.readUnsignedByte().toInt() == 1
        return LocationClickEvent(x.tiles, y.tiles, id, pressed)
    }
}

class Oploc2Packet : GamePacketDecoder(99, FixedSize(7)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): GameEvent {
        val x = data.readUnsignedShortADD()
        val pressed = data.readUnsignedByteADD().toInt() == 1
        val id = data.readUnsignedShortLEADD()
        val y = data.readUnsignedShortADD()
        return LocationClickEvent(x.tiles, y.tiles, id, pressed)
    }
}

class Oploc3Packet : GamePacketDecoder(13, FixedSize(7)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): GameEvent {
        val pressed = data.readUnsignedByteSUB().toInt() == 1
        val x = data.readUnsignedShortLEADD()
        val y = data.readUnsignedShortLEADD()
        val id = data.readUnsignedShortLEADD()
        return LocationClickEvent(x.tiles, y.tiles, id, pressed)
    }
}

class Oploc4Packet : GamePacketDecoder(100, FixedSize(7)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): GameEvent {
        val x = data.readUnsignedShortLE()
        val id = data.readUnsignedShortLE()
        val y = data.readUnsignedShortLE()
        val pressed = data.readUnsignedByteNEG().toInt() == 1
        return LocationClickEvent(x.tiles, y.tiles, id, pressed)
    }
}