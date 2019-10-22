package io.guthix.oldscape.server.net.state.js5

import io.guthix.oldscape.server.net.PacketInboundHandler
import io.netty.channel.ChannelHandlerContext

class Js5Handler : PacketInboundHandler<Js5FileRequest>() {
    override fun channelRead0(ctx: ChannelHandlerContext, msg: Js5FileRequest) {
    }
}