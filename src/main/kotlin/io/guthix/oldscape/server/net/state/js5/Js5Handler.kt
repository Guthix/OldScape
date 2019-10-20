package io.guthix.oldscape.server.net.state.js5

import io.guthix.oldscape.server.net.PacketInboundHandler
import io.netty.channel.ChannelHandlerContext
import kotlinx.io.IOException

class Js5Handler(private val transferCache: Js5TransferCache) : PacketInboundHandler<Js5FileRequest>() {
    override fun channelRead0(ctx: ChannelHandlerContext, msg: Js5FileRequest) {
        val data = transferCache.containers[msg.indexFileId]?.get(msg.containerId) ?: throw IOException(
            "Could not find index file ${msg.indexFileId} container ${msg.containerId}"
        )
        ctx.writeAndFlush(data)
    }
}