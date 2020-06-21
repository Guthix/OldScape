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

import io.guthix.buffer.*
import io.guthix.oldscape.server.dimensions.tiles
import io.guthix.oldscape.server.event.LocationClickEvent
import io.guthix.oldscape.server.event.PlayerGameEvent
import io.guthix.oldscape.server.net.game.FixedSize
import io.guthix.oldscape.server.net.game.GamePacketDecoder
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Player
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class Oploc1Packet : GamePacketDecoder(76, FixedSize(7)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val pressed = buf.readUnsignedByteSUB().toInt() == 1
        val x = buf.readUnsignedShortLEADD()
        val id = buf.readUnsignedShortLE()
        val y = buf.readUnsignedShortLE()
        return LocationClickEvent(x.tiles, y.tiles, id, pressed, player, world)
    }
}

class Oploc2Packet : GamePacketDecoder(36, FixedSize(7)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val id = buf.readUnsignedShort()
        val x = buf.readUnsignedShortLEADD()
        val y = buf.readUnsignedShortLEADD()
        val pressed = buf.readUnsignedByteNEG().toInt() == 1
        return LocationClickEvent(x.tiles, y.tiles, id, pressed, player, world)
    }
}

class Oploc3Packet : GamePacketDecoder(89, FixedSize(7)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val x = buf.readUnsignedShortADD()
        val y = buf.readUnsignedShortLEADD()
        val pressed = buf.readUnsignedByteADD().toInt() == 1
        val id = buf.readUnsignedShortADD()
        return LocationClickEvent(x.tiles, y.tiles, id, pressed, player, world)
    }
}

class Oploc4Packet : GamePacketDecoder(81, FixedSize(7)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val id = buf.readUnsignedShortADD()
        val pressed = buf.readUnsignedByte().toInt() == 1
        val x = buf.readUnsignedShort()
        val y = buf.readUnsignedShortLE()
        return LocationClickEvent(x.tiles, y.tiles, id, pressed, player, world)
    }
}

class Oploc5Packet : GamePacketDecoder(67, FixedSize(7)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val id = buf.readUnsignedShortLEADD()
        val x = buf.readUnsignedShortLE()
        val pressed = buf.readUnsignedByte().toInt() == 1
        val y = buf.readUnsignedShort()
        return LocationClickEvent(x.tiles, y.tiles, id, pressed, player, world)
    }
}