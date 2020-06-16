/**
 * This file is part of Guthix OldScape-Server.
 *
 * Guthix OldScape-Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.net

import io.guthix.cache.js5.container.Js5Store
import io.guthix.oldscape.server.net.service.ServiceDecoder
import io.guthix.oldscape.server.net.service.ServiceHandler
import io.guthix.oldscape.server.world.World
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.WriteBufferWaterMark
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import mu.KotlinLogging
import java.math.BigInteger

private val logger = KotlinLogging.logger { }

class OldScapeServer(
    private val revision: Int,
    private val port: Int,
    private val rsaExp: BigInteger,
    private val rsaMod: BigInteger,
    private val world: World,
    private val store: Js5Store
) {
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
                            ServiceHandler(revision, rsaExp, rsaMod, world, store)
                        )
                    }
                })
            }
            val bind = bootstrap.bind(port).sync().addListener {
                if (it.isSuccess) {
                    logger.info { "Server now listening to port $port" }
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