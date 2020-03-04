package io.guthix.oldscape.server.net.state.game.outp

import io.guthix.oldscape.server.net.state.game.FixedSize
import io.guthix.oldscape.server.net.state.game.OutGameEvent
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class UpdateRunenergyPacket(private val energy: Int) : OutGameEvent {
    override val opcode = 27

    override val size = FixedSize(UpdateInvStopTransmitPacket.STATIC_SIZE)

    override fun encode(ctx: ChannelHandlerContext): ByteBuf {
        val buf = ctx.alloc().buffer(STATIC_SIZE)
        buf.writeByte(energy)
        return buf
    }

    companion object {
        const val STATIC_SIZE = Byte.SIZE_BYTES
    }
}