package io.guthix.oldscape.server.net.state.login

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class SessionIdEncoder : MessageToByteEncoder<Long>() {
    override fun encode(ctx: ChannelHandlerContext, msg: Long, out: ByteBuf) {
        out.writeLong(msg)
    }
}