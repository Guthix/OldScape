package io.guthix.oldscape.server.net.state.login

import io.guthix.oldscape.server.net.IncPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class LoginEncoder : MessageToByteEncoder<IncPacket>() {
    override fun encode(ctx: ChannelHandlerContext, msg: IncPacket, out: ByteBuf) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}