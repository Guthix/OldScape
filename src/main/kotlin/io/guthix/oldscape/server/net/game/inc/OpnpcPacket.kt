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
import io.guthix.oldscape.server.event.*
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
        val pressed = buf.readUnsignedByteNeg().toInt() == 1
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
        val pressed = buf.readUnsignedByteNeg().toInt() == 1
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
        val index = buf.readUnsignedShortAdd()
        val pressed = buf.readUnsignedByteNeg().toInt() == 1
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
        val pressed = buf.readUnsignedByteSub().toInt() == 1
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
        val id = buf.readUnsignedShortLE()
        return NpcExamineEvent(id, player, world)
    }
}

class OpnpctPacket : GamePacketDecoder(50, FixedSize(9)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val ctrlPressed = buf.readUnsignedByteSub().toInt() == 1
        val bitpack = buf.readInt()
        val interfaceId = bitpack shr Short.SIZE_BITS
        val interfaceSlotId = bitpack and 0xFFFF
        val something = buf.readUnsignedShort()
        val npcId = buf.readUnsignedShort()
        return IfOnNpcEvent(npcId, interfaceId, interfaceSlotId, ctrlPressed, something, player, world)
    }
}

class OpnpcuPacket : GamePacketDecoder(39, FixedSize(11)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val invSlot = buf.readUnsignedShort()
        val objId = buf.readUnsignedShort()
        val bitpack = buf.readInt()
        val interfaceId = bitpack shr Short.SIZE_BITS
        val interfaceSlotId = bitpack and 0xFFFF
        val ctrlPressed = buf.readUnsignedByteAdd().toInt() == 1
        val npcId = buf.readUnsignedShort()
        return ObjOnNpcEvent(npcId, objId, interfaceId, interfaceSlotId, invSlot, ctrlPressed, player, world)
    }
}


