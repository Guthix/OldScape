package io.guthix.oldscape.server.net.state.game.outp

import io.guthix.buffer.writeByteADD
import io.guthix.oldscape.server.net.state.game.FixedSize
import io.guthix.oldscape.server.net.state.game.GamePacket
import io.guthix.oldscape.server.net.state.game.OutGameEvent
import io.netty.channel.ChannelHandlerContext

class VarpSmallPacket(private val id: Int, private val state: Int) : OutGameEvent {
    override fun encode(ctx: ChannelHandlerContext): GamePacket {
        val buf = ctx.alloc().buffer(STATIC_SIZE)
        buf.writeShort(state)
        buf.writeByteADD(id)
        return GamePacket(24, FixedSize(VarpLargePacket.STATIC_SIZE), buf)
    }

    companion object {
        const val STATIC_SIZE = Short.SIZE_BYTES + Byte.SIZE_BYTES
    }
}