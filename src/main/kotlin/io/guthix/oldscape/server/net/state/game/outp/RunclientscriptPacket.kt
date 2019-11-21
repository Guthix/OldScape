package io.guthix.oldscape.server.net.state.game.outp

import io.guthix.buffer.writeStringCP1252
import io.guthix.oldscape.server.net.state.game.GamePacket
import io.guthix.oldscape.server.net.state.game.OutGameEvent
import io.guthix.oldscape.server.net.state.game.VarShortSize
import io.netty.channel.ChannelHandlerContext

class RunclientscriptPacket(private val id: Int, private val params: Array<Any>) : OutGameEvent {
    override fun encode(ctx: ChannelHandlerContext): GamePacket {
        val buf = ctx.alloc().buffer()
        val argumentListIdentifier = StringBuilder()
        for (param in params.reversed()) {
            if (param is String) {
                argumentListIdentifier.append("s")
            } else {
                argumentListIdentifier.append("i")
            }
        }
        buf.writeStringCP1252(argumentListIdentifier.toString())
        for (param in params) {
            if (param is String) {
                buf.writeStringCP1252(param)
            } else {
                buf.writeInt(param as Int)
            }
        }
        buf.writeInt(id)
        return GamePacket(83, VarShortSize, buf)
    }
}