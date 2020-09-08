/*
 * Copyright 2018-2020 Guthix
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.guthix.oldscape.server.net

import io.guthix.js5.container.Js5Store
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