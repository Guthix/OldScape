package io.guthix.oldscape.server.net.state.game.outp

import io.guthix.buffer.writeShortADD
import io.guthix.oldscape.server.net.state.game.FixedSize
import io.guthix.oldscape.server.net.state.game.GamePacket
import io.guthix.oldscape.server.net.state.game.OutGameEvent
import io.netty.channel.ChannelHandlerContext

class IfOpentopPacket(private val interfaceId: Int) : OutGameEvent {
    override fun encode(ctx: ChannelHandlerContext): GamePacket {
        val buf = ctx.alloc().buffer(Short.SIZE_BYTES)
        buf.writeShortADD(interfaceId)
        return GamePacket(0, FixedSize(Short.SIZE_BYTES), buf)
    }
}