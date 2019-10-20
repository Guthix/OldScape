package io.guthix.oldscape.server.net.state.login

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class LoginDecoder : ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext, inc: ByteBuf, out: MutableList<Any>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}