/*
 * Copyright 2018-2021 Guthix
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
import io.guthix.oldscape.server.event.*
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
        val x = buf.readUnsignedShortAddLE().tiles
        val id = buf.readUnsignedShortLE()
        val y = buf.readUnsignedShortLE().tiles
        return LocClickEvent(x, y, id, pressed, player, world)
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
        val x = buf.readUnsignedShortAddLE().tiles
        val y = buf.readUnsignedShortAddLE().tiles
        val pressed = buf.readUnsignedByteNeg().toInt() == 1
        return LocClickEvent(x, y, id, pressed, player, world)
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
        val x = buf.readUnsignedShortAdd().tiles
        val y = buf.readUnsignedShortAddLE().tiles
        val pressed = buf.readUnsignedByteAdd().toInt() == 1
        val id = buf.readUnsignedShortAdd()
        return LocClickEvent(x, y, id, pressed, player, world)
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
        val x = buf.readUnsignedShort().tiles
        val y = buf.readUnsignedShortLE().tiles
        return LocClickEvent(x, y, id, pressed, player, world)
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
        val id = buf.readUnsignedShortAddLE()
        val x = buf.readUnsignedShortLE().tiles
        val pressed = buf.readUnsignedByte().toInt() == 1
        val y = buf.readUnsignedShort().tiles
        return LocClickEvent(x, y, id, pressed, player, world)
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

class OploctPacket : GamePacketDecoder(45, FixedSize(13)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val something = buf.readUnsignedShort()
        val bitpack = buf.readInt()
        val interfaceId = bitpack shr Short.SIZE_BITS
        val interfaceSlotId = bitpack and 0xFFFF
        val locId = buf.readShortAdd().toInt()
        val y = buf.readShortLE().toInt().tiles
        val ctrlPressed = buf.readUnsignedByteSub().toInt() == 1
        val x = buf.readShortLE().toInt().tiles
        return IfOnLocEvent(locId, interfaceId, interfaceSlotId, x, y, ctrlPressed, something, player, world)
    }
}

class OplocuPacket : GamePacketDecoder(14, FixedSize(15)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val invSlot = buf.readUnsignedShortAdd()
        val bitpack = buf.readIntIME()
        val interfaceId = bitpack shr Short.SIZE_BITS
        val interfaceSlotId = bitpack and 0xFFFF
        val y = buf.readShortLE().toInt().tiles
        val x = buf.readShortLE().toInt().tiles
        val locId = buf.readShortLE().toInt()
        val objId = buf.readShortLE().toInt()
        val ctrlPressed = buf.readUnsignedByteAdd().toInt() == 1
        return ObjOnLocEvent(locId, objId, x, y, interfaceId, interfaceSlotId, invSlot, ctrlPressed, player, world)
    }
}