package io.guthix.oldscape.server.net

import io.guthix.cache.js5.container.disk.Js5DiskStore
import io.guthix.oldscape.server.net.state.js5.Js5TransferCache
import io.guthix.oldscape.server.net.state.service.ServiceDecoder
import io.guthix.oldscape.server.net.state.service.ServiceHandler
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.WriteBufferWaterMark
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

class OldScapeServer(private val revision: Int, private val port: Int, private val transferCache: Js5TransferCache) {
    fun run() {
        val bossGroup = NioEventLoopGroup()
        val loopGroup = NioEventLoopGroup()
        try {
            val bootstrap = ServerBootstrap().apply {
                group(bossGroup, loopGroup)
                channel(NioServerSocketChannel::class.java)
                childOption(ChannelOption.SO_KEEPALIVE, true)
                childOption(ChannelOption.TCP_NODELAY, true)
                childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, WriteBufferWaterMark(8192, 131072))
                childHandler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(channel: SocketChannel) {
                        channel.pipeline().addLast(ServiceDecoder::class.qualifiedName, ServiceDecoder())
                        channel.pipeline().addLast(ServiceHandler::class.qualifiedName,
                            ServiceHandler(revision, transferCache)
                        )
                    }
                })
            }
            val bind = bootstrap.bind(port).sync().addListener {
                if (it.isSuccess) {
                    logger.info{ "Server now listening to port $port" }
                } else {
                    logger.error(it.cause()) { "Server failed to connect to port $port" }
                }
            }
            bind.channel().closeFuture().sync()
        } finally {
            bossGroup.shutdownGracefully()
            loopGroup.shutdownGracefully()
        }
    }
}