package io.guthix.oldscape.server.net.state.game.outp

import io.guthix.buffer.writeIntME
import io.guthix.oldscape.server.net.state.game.FixedSize
import io.guthix.oldscape.server.net.state.game.OutGameEvent
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class IfMovesubPacket(
    private val fromTopInterface: Int,
    private val fromSubInterface: Int,
    private val toTopInterface: Int,
    private val toSubInterface: Int
) : OutGameEvent() {
    override val opcode = 78

    override val size = FixedSize(STATIC_SIZE)

    override fun encode(ctx: ChannelHandlerContext): ByteBuf {
        val buf = ctx.alloc().buffer(STATIC_SIZE)
        buf.writeIntME((fromTopInterface shl Short.SIZE_BITS) or fromSubInterface)
        buf.writeInt((toTopInterface shl Short.SIZE_BITS) or toSubInterface)
        return buf
    }

    companion object {
        const val STATIC_SIZE = Int.SIZE_BYTES + Int.SIZE_BYTES
    }
}