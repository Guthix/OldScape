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

import io.guthix.oldscape.server.event.ButtonClickEvent
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