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

import io.guthix.buffer.readUnsignedIntIME
import io.guthix.buffer.readUnsignedShortAdd
import io.guthix.oldscape.server.event.ButtonClickEvent
import io.guthix.oldscape.server.event.InvObjMovedEvent
import io.guthix.oldscape.server.net.game.FixedSize
import io.guthix.oldscape.server.net.game.GamePacketDecoder
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Player
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class IfButton1Packet : GamePacketDecoder(57, FixedSize(8)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): ButtonClickEvent {
        val bitpack = buf.readUnsignedInt().toInt()
        val componentId = buf.readUnsignedShort()
        val slotId = buf.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, componentId, slotId, 1, player, world)
    }
}

class IfButton2Packet : GamePacketDecoder(73, FixedSize(8)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): ButtonClickEvent {
        val bitpack = buf.readUnsignedInt().toInt()
        val componentId = buf.readUnsignedShort()
        val slotId = buf.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, componentId, slotId, 2, player, world)
    }
}

class IfButton3Packet : GamePacketDecoder(71, FixedSize(8)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): ButtonClickEvent {
        val bitpack = buf.readUnsignedInt().toInt()
        val componentId = buf.readUnsignedShort()
        val slotId = buf.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, componentId, slotId, 3, player, world)
    }
}

class IfButton4Packet : GamePacketDecoder(19, FixedSize(8)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): ButtonClickEvent {
        val bitpack = buf.readUnsignedInt().toInt()
        val componentId = buf.readUnsignedShort()
        val slotId = buf.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, componentId, slotId, 4, player, world)
    }
}

class IfButton5Packet : GamePacketDecoder(62, FixedSize(8)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): ButtonClickEvent {
        val bitpack = buf.readUnsignedInt().toInt()
        val componentId = buf.readUnsignedShort()
        val slotId = buf.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, componentId, slotId, 5, player, world)
    }
}

class IfButton6Packet : GamePacketDecoder(23, FixedSize(8)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): ButtonClickEvent {
        val bitpack = buf.readUnsignedInt().toInt()
        val componentId = buf.readUnsignedShort()
        val slotId = buf.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, componentId, slotId, 6, player, world)
    }
}

class IfButton7Packet : GamePacketDecoder(49, FixedSize(8)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): ButtonClickEvent {
        val bitpack = buf.readUnsignedInt().toInt()
        val componentId = buf.readUnsignedShort()
        val slotId = buf.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, componentId, slotId, 7, player, world)
    }
}

class IfButton8Packet : GamePacketDecoder(55, FixedSize(8)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): ButtonClickEvent {
        val bitpack = buf.readUnsignedInt().toInt()
        val componentId = buf.readUnsignedShort()
        val slotId = buf.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, componentId, slotId, 8, player, world)
    }
}

class IfButton9Packet : GamePacketDecoder(96, FixedSize(8)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): ButtonClickEvent {
        val bitpack = buf.readUnsignedInt().toInt()
        val componentId = buf.readUnsignedShort()
        val slotId = buf.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, componentId, slotId, 9, player, world)
    }
}

class IfButton10Packet : GamePacketDecoder(48, FixedSize(8)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): ButtonClickEvent {
        val bitpack = buf.readUnsignedInt().toInt()
        val componentId = buf.readUnsignedShort()
        val slotId = buf.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, componentId, slotId, 10, player, world)
    }
}

class IfButtonDPacket : GamePacketDecoder(88, FixedSize(9)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): InvObjMovedEvent {
        val toSlot = buf.readUnsignedShortLE()
        val fromSlot = buf.readUnsignedShortAdd()
        val someByte = buf.readUnsignedByte().toInt()
        val bitpack = buf.readUnsignedIntIME()
        val interfaceId = (bitpack shr Short.SIZE_BITS).toInt()
        val interfaceSlotId = (bitpack and 0xFFFF).toInt()
        return InvObjMovedEvent(fromSlot, toSlot, interfaceId, interfaceSlotId, someByte, player, world)
    }
}