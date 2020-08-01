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
import io.guthix.buffer.readUnsignedShortAdd
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
        val id = buf.readUnsignedShortLE()
        val x = buf.readUnsignedShortLE()
        val y = buf.readUnsignedShortLE()
        val buttonPressed = buf.readUnsignedByteNeg().toInt() == 1
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
        val x = buf.readUnsignedShortAdd()
        val buttonPressed = buf.readUnsignedByteAdd().toInt() == 1
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
        val y = buf.readUnsignedShortAdd()
        val id = buf.readUnsignedShortLE()
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
        val y = buf.readUnsignedShortAdd()
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
        val y = buf.readUnsignedShortAdd()
        val buttonPressed = buf.readUnsignedByteAdd().toInt() == 1
        val x = buf.readUnsignedShortAdd()
        return ObjectClickEvent(id, x.tiles, y.tiles, buttonPressed, 5, player, world)
    }
}