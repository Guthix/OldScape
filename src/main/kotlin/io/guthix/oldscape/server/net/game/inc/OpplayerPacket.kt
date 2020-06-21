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
import io.guthix.oldscape.server.event.PlayerClickEvent
import io.guthix.oldscape.server.event.PlayerGameEvent
import io.guthix.oldscape.server.net.game.FixedSize
import io.guthix.oldscape.server.net.game.GamePacketDecoder
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Player
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class Opplayer1Packet : GamePacketDecoder(60, FixedSize(3)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val buttonPressed = buf.readUnsignedByte().toInt() == 1
        val index = buf.readUnsignedShortLEADD()
        return PlayerClickEvent(index, buttonPressed, 1, player, world)
    }
}

class Opplayer2Packet : GamePacketDecoder(25, FixedSize(3)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val index = buf.readUnsignedShortADD()
        val buttonPressed = buf.readUnsignedByteADD().toInt() == 1
        return PlayerClickEvent(index, buttonPressed, 2, player, world)
    }
}

class Opplayer3Packet : GamePacketDecoder(59, FixedSize(3)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val buttonPressed = buf.readUnsignedByteADD().toInt() == 1
        val index = buf.readUnsignedShort()
        return PlayerClickEvent(index, buttonPressed, 3, player, world)
    }
}

class Opplayer4Packet : GamePacketDecoder(75, FixedSize(3)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val index = buf.readUnsignedShort()
        val buttonPressed = buf.readUnsignedByteSUB().toInt() == 1
        return PlayerClickEvent(index, buttonPressed, 4, player, world)
    }
}

class Opplayer5Packet : GamePacketDecoder(51, FixedSize(3)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val buttonPressed = buf.readUnsignedByteADD().toInt() == 1
        val index = buf.readUnsignedShort()
        return PlayerClickEvent(index, buttonPressed, 5, player, world)
    }
}

class Opplayer6Packet : GamePacketDecoder(43, FixedSize(3)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val buttonPressed = buf.readUnsignedByteNEG().toInt() == 1
        val index = buf.readUnsignedShortLEADD()
        return PlayerClickEvent(index, buttonPressed, 6, player, world)
    }
}

class Opplayer7Packet : GamePacketDecoder(94, FixedSize(3)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val buttonPressed = buf.readUnsignedByte().toInt() == 1
        val index = buf.readUnsignedShort()
        return PlayerClickEvent(index, buttonPressed, 7, player, world)
    }
}

class Opplayer8Packet : GamePacketDecoder(40, FixedSize(3)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val buttonPressed = buf.readUnsignedByte().toInt() == 1
        val index = buf.readUnsignedShort()
        return PlayerClickEvent(index, buttonPressed, 8, player, world)
    }
}