package io.guthix.oldscape.server.net.state.game.inp

import io.guthix.oldscape.server.event.GameEvent
import io.guthix.oldscape.server.net.state.game.FixedSize
import io.guthix.oldscape.server.net.state.game.GamePacketDecoder
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

data class MouseClickEvent(val isLeftClick: Boolean, val presDuration: Int, val mouseX: Int, val mouseY: Int) : GameEvent

class EventMouseClickPacket : GamePacketDecoder(37, FixedSize(7)) {
    override fun decode(data: ByteBuf, ctx: ChannelHandlerContext): MouseClickEvent {
        val bitPack = data.readShort().toInt()
        val mouseX = data.readShort().toInt()
        val mouseY = data.readShort().toInt()
        return MouseClickEvent(bitPack and 0x1 == 1, bitPack shr 1, mouseX, mouseY)
    }
}