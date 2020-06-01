/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
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
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class IfButton1Packet : GamePacketDecoder(57, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ButtonClickEvent {
        val bitpack = data.readUnsignedInt().toInt()
        val componentId = data.readUnsignedShort()
        val slotId = data.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, componentId, slotId, 1)
    }
}

class IfButton2Packet : GamePacketDecoder(73, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ButtonClickEvent {
        val bitpack = data.readUnsignedInt().toInt()
        val componentId = data.readUnsignedShort()
        val slotId = data.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, componentId, slotId, 2)
    }
}

class IfButton3Packet : GamePacketDecoder(71, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ButtonClickEvent {
        val bitpack = data.readUnsignedInt().toInt()
        val componentId = data.readUnsignedShort()
        val slotId = data.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, componentId, slotId, 3)
    }
}

class IfButton4Packet : GamePacketDecoder(19, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ButtonClickEvent {
        val bitpack = data.readUnsignedInt().toInt()
        val componentId = data.readUnsignedShort()
        val slotId = data.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, componentId, slotId, 4)
    }
}

class IfButton5Packet : GamePacketDecoder(62, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ButtonClickEvent {
        val bitpack = data.readUnsignedInt().toInt()
        val componentId = data.readUnsignedShort()
        val slotId = data.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, componentId, slotId, 5)
    }
}

class IfButton6Packet : GamePacketDecoder(23, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ButtonClickEvent {
        val bitpack = data.readUnsignedInt().toInt()
        val componentId = data.readUnsignedShort()
        val slotId = data.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, componentId, slotId, 6)
    }
}

class IfButton7Packet : GamePacketDecoder(49, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ButtonClickEvent {
        val bitpack = data.readUnsignedInt().toInt()
        val componentId = data.readUnsignedShort()
        val slotId = data.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, componentId, slotId, 7)
    }
}

class IfButton8Packet : GamePacketDecoder(55, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ButtonClickEvent {
        val bitpack = data.readUnsignedInt().toInt()
        val componentId = data.readUnsignedShort()
        val slotId = data.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, componentId, slotId, 8)
    }
}

class IfButton9Packet : GamePacketDecoder(96, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ButtonClickEvent {
        val bitpack = data.readUnsignedInt().toInt()
        val componentId = data.readUnsignedShort()
        val slotId = data.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, componentId, slotId, 9)
    }
}

class IfButton10Packet : GamePacketDecoder(48, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ButtonClickEvent {
        val bitpack = data.readUnsignedInt().toInt()
        val componentId = data.readUnsignedShort()
        val slotId = data.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, componentId, slotId, 10)
    }
}