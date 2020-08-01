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
        val index = buf.readUnsignedShortLE()
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
        val index = buf.readUnsignedShortAdd()
        val buttonPressed = buf.readUnsignedByteAdd().toInt() == 1
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
        val buttonPressed = buf.readUnsignedByteAdd().toInt() == 1
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
        val buttonPressed = buf.readUnsignedByteSub().toInt() == 1
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
        val buttonPressed = buf.readUnsignedByteAdd().toInt() == 1
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
        val buttonPressed = buf.readUnsignedByteNeg().toInt() == 1
        val index = buf.readUnsignedShortLE()
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