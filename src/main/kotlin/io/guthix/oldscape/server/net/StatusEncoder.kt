package io.guthix.oldscape.server.net

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class StatusEncoder : MessageToByteEncoder<StatusResponse>() {
    override fun encode(ctx: ChannelHandlerContext, msg: StatusResponse, out: ByteBuf) {
        out.writeByte(msg.opcode)
    }
}