package io.guthix.oldscape.server.net.state.game.inp

import io.guthix.oldscape.server.event.imp.ButtonClickEvent
import io.guthix.oldscape.server.net.state.game.FixedSize
import io.guthix.oldscape.server.net.state.game.GamePacketDecoder
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class IfButton1Packet : GamePacketDecoder(62, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ButtonClickEvent {
        val bitpack = data.readUnsignedInt().toInt()
        val componentId = data.readUnsignedShort()
        val slotId = data.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BYTES, bitpack and 0xFFFF, componentId, slotId, 1)
    }
}

class IfButton2Packet : GamePacketDecoder(39, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ButtonClickEvent {
        val bitpack = data.readUnsignedInt().toInt()
        val componentId = data.readUnsignedShort()
        val slotId = data.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BYTES, bitpack and 0xFFFF, componentId, slotId, 2)
    }
}

class IfButton3Packet : GamePacketDecoder(69, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ButtonClickEvent {
        val bitpack = data.readUnsignedInt().toInt()
        val componentId = data.readUnsignedShort()
        val slotId = data.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BYTES, bitpack and 0xFFFF, componentId, slotId, 3)
    }
}

class IfButton4Packet : GamePacketDecoder(71, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ButtonClickEvent {
        val bitpack = data.readUnsignedInt().toInt()
        val componentId = data.readUnsignedShort()
        val slotId = data.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BYTES, bitpack and 0xFFFF, componentId, slotId, 4)
    }
}

class IfButton5Packet : GamePacketDecoder(52, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ButtonClickEvent {
        val bitpack = data.readUnsignedInt().toInt()
        val componentId = data.readUnsignedShort()
        val slotId = data.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BYTES, bitpack and 0xFFFF, componentId, slotId, 5)
    }
}

class IfButton6Packet : GamePacketDecoder(84, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ButtonClickEvent {
        val bitpack = data.readUnsignedInt().toInt()
        val componentId = data.readUnsignedShort()
        val slotId = data.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BYTES, bitpack and 0xFFFF, componentId, slotId, 6)
    }
}

class IfButton7Packet : GamePacketDecoder(91, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ButtonClickEvent {
        val bitpack = data.readUnsignedInt().toInt()
        val componentId = data.readUnsignedShort()
        val slotId = data.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BYTES, bitpack and 0xFFFF, componentId, slotId, 7)
    }
}

class IfButton8Packet : GamePacketDecoder(23, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ButtonClickEvent {
        val bitpack = data.readUnsignedInt().toInt()
        val componentId = data.readUnsignedShort()
        val slotId = data.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BYTES, bitpack and 0xFFFF, componentId, slotId, 8)
    }
}

class IfButton9Packet : GamePacketDecoder(7, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ButtonClickEvent {
        val bitpack = data.readUnsignedInt().toInt()
        val componentId = data.readUnsignedShort()
        val slotId = data.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BYTES, bitpack and 0xFFFF, componentId, slotId, 9)
    }
}

class IfButton10Packet : GamePacketDecoder(80, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ButtonClickEvent {
        val bitpack = data.readUnsignedInt().toInt()
        val componentId = data.readUnsignedShort()
        val slotId = data.readUnsignedShort()
        return ButtonClickEvent(bitpack shr Short.SIZE_BYTES, bitpack and 0xFFFF, componentId, slotId, 10)
    }
}