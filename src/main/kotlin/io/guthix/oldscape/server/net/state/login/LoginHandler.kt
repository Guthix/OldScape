package io.guthix.oldscape.server.net.state.login

import io.guthix.oldscape.server.net.IncPacket
import io.guthix.oldscape.server.net.PacketInboundHandler
import io.netty.channel.ChannelHandlerContext

class LoginHandler(val sessionId: Long) : PacketInboundHandler<IncPacket>() {
    override fun channelRead0(ctx: ChannelHandlerContext?, msg: IncPacket?) {
        println("Received lo0gin request ${msg as LoginRequest}")
    }
}