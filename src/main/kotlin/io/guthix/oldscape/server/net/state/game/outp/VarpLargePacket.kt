package io.guthix.oldscape.server.net.state.game.outp

import io.guthix.buffer.writeIntME
import io.guthix.oldscape.server.net.state.game.FixedSize
import io.guthix.oldscape.server.net.state.game.GamePacket
import io.guthix.oldscape.server.net.state.game.OutGameEvent
import io.netty.channel.ChannelHandlerContext

class VarpLargePacket(private val id: Int, private val state: Int) : OutGameEvent {
    override fun encode(ctx: ChannelHandlerContext): GamePacket {
        val buf = ctx.alloc().buffer(VarpSmallPacket.STATIC_SIZE)
        buf.writeIntME(state)
        buf.writeShortLE(id)
        return GamePacket(34, FixedSize(STATIC_SIZE), buf)
    }

    companion object {
        const val STATIC_SIZE = Int.SIZE_BYTES + Short.SIZE_BYTES
    }
}