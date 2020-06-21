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

import io.guthix.buffer.readUnsignedByteADD
import io.guthix.buffer.readUnsignedByteNEG
import io.guthix.buffer.readUnsignedShortADD
import io.guthix.buffer.readUnsignedShortLEADD
import io.guthix.oldscape.server.dimensions.tiles
import io.guthix.oldscape.server.event.ObjectClickEvent
import io.guthix.oldscape.server.event.PlayerGameEvent
import io.guthix.oldscape.server.net.game.FixedSize
import io.guthix.oldscape.server.net.game.GamePacketDecoder
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Player
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class Opobj1Packet : GamePacketDecoder(32, FixedSize(7)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val id = buf.readUnsignedShortLEADD()
        val x = buf.readUnsignedShortLEADD()
        val y = buf.readUnsignedShortLE()
        val buttonPressed = buf.readUnsignedByteNEG().toInt() == 1
        return ObjectClickEvent(id, x.tiles, y.tiles, buttonPressed, 1, player, world)
    }
}

class Opobj2Packet : GamePacketDecoder(80, FixedSize(7)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val y = buf.readUnsignedShort()
        val x = buf.readUnsignedShortADD()
        val buttonPressed = buf.readUnsignedByteADD().toInt() == 1
        val id = buf.readUnsignedShort()
        return ObjectClickEvent(id, x.tiles, y.tiles, buttonPressed, 2, player, world)
    }
}

class Opobj3Packet : GamePacketDecoder(68, FixedSize(7)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val buttonPressed = buf.readUnsignedByte().toInt() == 1
        val x = buf.readUnsignedShortLE()
        val y = buf.readUnsignedShortADD()
        val id = buf.readUnsignedShortLEADD()
        return ObjectClickEvent(id, x.tiles, y.tiles, buttonPressed, 3, player, world)
    }
}

class Opobj4Packet : GamePacketDecoder(20, FixedSize(7)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val buttonPressed = buf.readUnsignedByte().toInt() == 1
        val id = buf.readUnsignedShortLE()
        val x = buf.readUnsignedShortLE()
        val y = buf.readUnsignedShortADD()
        return ObjectClickEvent(id, x.tiles, y.tiles, buttonPressed, 4, player, world)
    }
}

class Opobj5Packet : GamePacketDecoder(28, FixedSize(7)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val id = buf.readUnsignedShort()
        val y = buf.readUnsignedShortADD()
        val buttonPressed = buf.readUnsignedByteADD().toInt() == 1
        val x = buf.readUnsignedShortADD()
        return ObjectClickEvent(id, x.tiles, y.tiles, buttonPressed, 5, player, world)
    }
}