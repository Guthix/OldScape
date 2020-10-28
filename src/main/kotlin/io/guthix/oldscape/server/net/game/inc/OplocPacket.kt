/*
 * Copyright 2018-2020 Guthix
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.guthix.oldscape.server.net.game.inc

import io.guthix.buffer.readUnsignedByteAdd
import io.guthix.buffer.readUnsignedByteNeg
import io.guthix.buffer.readUnsignedByteSub
import io.guthix.buffer.readUnsignedShortAdd
import io.guthix.oldscape.server.event.LocExamineEvent
import io.guthix.oldscape.server.event.LocationClickEvent
import io.guthix.oldscape.server.event.PlayerGameEvent
import io.guthix.oldscape.server.net.game.FixedSize
import io.guthix.oldscape.server.net.game.GamePacketDecoder
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.map.dim.tiles
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
        val pressed = buf.readUnsignedByteSub().toInt() == 1
        val x = buf.readUnsignedShortLE()
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
        val x = buf.readUnsignedShortLE()
        val y = buf.readUnsignedShortLE()
        val pressed = buf.readUnsignedByteNeg().toInt() == 1
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
        val x = buf.readUnsignedShortAdd()
        val y = buf.readUnsignedShortLE()
        val pressed = buf.readUnsignedByteAdd().toInt() == 1
        val id = buf.readUnsignedShortAdd()
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
        val id = buf.readUnsignedShortAdd()
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
        val id = buf.readUnsignedShortLE()
        val x = buf.readUnsignedShortLE()
        val pressed = buf.readUnsignedByte().toInt() == 1
        val y = buf.readUnsignedShort()
        return LocationClickEvent(x.tiles, y.tiles, id, pressed, player, world)
    }
}

class Oploc6Packet : GamePacketDecoder(77, FixedSize(2)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val id = buf.readUnsignedShortAdd()
        return LocExamineEvent(id, player, world)
    }
}