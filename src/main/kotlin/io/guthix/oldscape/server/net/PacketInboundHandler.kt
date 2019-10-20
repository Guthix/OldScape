package io.guthix.oldscape.server.net

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

interface IncPacket

abstract class PacketInboundHandler<P : IncPacket> : SimpleChannelInboundHandler<P>() {
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        if (!cause.message.equals("An existing connection was forcibly closed by the remote host")) {
            logger.error(cause) { "Error while handling message, closing connection." }
        }
        ctx.close()
    }
}