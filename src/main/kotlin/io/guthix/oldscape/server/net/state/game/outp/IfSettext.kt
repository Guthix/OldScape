package io.guthix.oldscape.server.net.state.game.outp

import io.guthix.buffer.writeIntME
import io.guthix.buffer.writeStringCP1252
import io.guthix.oldscape.server.net.state.game.GamePacket
import io.guthix.oldscape.server.net.state.game.OutGameEvent
import io.guthix.oldscape.server.net.state.game.VarShortSize
import io.netty.channel.ChannelHandlerContext

class IfSettext(
    private val interfaceId: Int,
    private val componentId: Int,
    private val message: String
) : OutGameEvent {
    override fun encode(ctx: ChannelHandlerContext): GamePacket {
        val buf = ctx.alloc().buffer(Int.SIZE_BYTES + message.length)
        buf.writeIntME((interfaceId shl 16) or componentId)
        buf.writeStringCP1252(message)
        return GamePacket(19, VarShortSize, buf)
    }
}