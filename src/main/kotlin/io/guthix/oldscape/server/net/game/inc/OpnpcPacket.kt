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
import io.guthix.oldscape.server.event.NpcClickEvent
import io.guthix.oldscape.server.event.NpcExamineEvent
import io.guthix.oldscape.server.event.PlayerGameEvent
import io.guthix.oldscape.server.net.game.FixedSize
import io.guthix.oldscape.server.net.game.GamePacketDecoder
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Player
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class Opnpc1Packet : GamePacketDecoder(56, FixedSize(3)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val index = buf.readUnsignedShortLE()
        val pressed = buf.readUnsignedByteNEG().toInt() == 1
        return NpcClickEvent(index, pressed, 1, player, world)
    }
}

class Opnpc2Packet : GamePacketDecoder(4, FixedSize(3)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val index = buf.readUnsignedShort()
        val pressed = buf.readUnsignedByteNEG().toInt() == 1
        return NpcClickEvent(index, pressed, 2, player, world)
    }
}

class Opnpc3Packet : GamePacketDecoder(46, FixedSize(3)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val index = buf.readUnsignedShortADD()
        val pressed = buf.readUnsignedByteNEG().toInt() == 1
        return NpcClickEvent(index, pressed, 3, player, world)
    }
}

class Opnpc4Packet : GamePacketDecoder(12, FixedSize(3)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val pressed = buf.readUnsignedByteSUB().toInt() == 1
        val index = buf.readUnsignedShortLE()
        return NpcClickEvent(index, pressed, 4, player, world)
    }
}

class Opnpc5Packet : GamePacketDecoder(37, FixedSize(3)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val pressed = buf.readUnsignedByte().toInt() == 1
        val index = buf.readUnsignedShort()
        return NpcClickEvent(index, pressed, 5, player, world)
    }
}

class Opnpc6Packet : GamePacketDecoder(91, FixedSize(2)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val id = buf.readUnsignedShortLEADD()
        return NpcExamineEvent(id, player, world)
    }
}


