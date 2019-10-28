package io.guthix.oldscape.server.net.state.js5

import io.guthix.oldscape.server.net.PacketInboundHandler
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.DefaultFileRegion
import java.nio.channels.FileChannel
import java.nio.file.Path

class Js5Handler : PacketInboundHandler<Js5FileRequest>() {
    override fun channelRead0(ctx: ChannelHandlerContext, msg: Js5FileRequest) {
        val indexFolder = Path.of(cacheDir + msg.indexFileId)
        val packet = indexFolder.resolve(msg.containerId.toString()).toFile()
        ctx.writeAndFlush(DefaultFileRegion(packet, 0, packet.length()))
    }

    companion object {
        const val cacheDir = "server\\build\\resources\\main\\cache\\"
    }
}