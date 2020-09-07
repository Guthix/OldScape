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
import io.guthix.oldscape.server.event.InvObjClickEvent
import io.guthix.oldscape.server.event.InvObjOnObjEvent
import io.guthix.oldscape.server.event.PlayerGameEvent
import io.guthix.oldscape.server.net.game.FixedSize
import io.guthix.oldscape.server.net.game.GamePacketDecoder
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Player
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class Opheld1Packet : GamePacketDecoder(97, FixedSize(8)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val itemId = buf.readUnsignedShortLE()
        val bitpack = buf.readIntLE()
        val inventorySlotId = buf.readUnsignedShort()
        return InvObjClickEvent(
            bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, itemId, inventorySlotId, 1, player, world
        )
    }
}

class Opheld2Packet : GamePacketDecoder(58, FixedSize(8)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val bitpack = buf.readInt()
        val itemId = buf.readUnsignedShortAdd()
        val inventorySlotId = buf.readUnsignedShortAddLE()
        return InvObjClickEvent(
            bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, itemId, inventorySlotId, 2, player, world
        )
    }
}

class Opheld3Packet : GamePacketDecoder(61, FixedSize(8)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val bitpack = buf.readInt()
        val itemId = buf.readUnsignedShortLE()
        val inventorySlotId = buf.readUnsignedShort()
        return InvObjClickEvent(
            bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, itemId, inventorySlotId, 3, player, world
        )
    }
}

class Opheld4Packet : GamePacketDecoder(13, FixedSize(8)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val inventorySlotId = buf.readUnsignedShortLE()
        val bitpack = buf.readIntLE()
        val itemId = buf.readUnsignedShortLE()
        return InvObjClickEvent(
            bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, itemId, inventorySlotId, 4, player, world
        )
    }
}

class Opheld5Packet : GamePacketDecoder(5, FixedSize(8)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val inventorySlotId = buf.readUnsignedShortLE()
        val bitpack = buf.readIntME()
        val itemId = buf.readUnsignedShortAdd()
        return InvObjClickEvent(
            bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, itemId, inventorySlotId, 5, player, world
        )
    }
}

class OpheldUPacket : GamePacketDecoder(98, FixedSize(16)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val toBitpack = buf.readIntIME()
        val fromSlot = buf.readShortAddLE().toInt()
        val fromBitpack = buf.readIntME()
        val toSlot = buf.readShortAdd().toInt()
        val toItem = buf.readShort().toInt()
        val fromItem = buf.readShortAdd().toInt()
        return InvObjOnObjEvent(
            fromBitpack shr Short.SIZE_BITS, fromBitpack and 0xFFFF, toBitpack shr Short.SIZE_BITS,
            toBitpack and 0xFFFF, fromSlot, toSlot, fromItem, toItem, player, world
        )
    }
}