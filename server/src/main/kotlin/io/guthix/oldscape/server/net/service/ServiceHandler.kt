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
package io.guthix.oldscape.server.net.service

import io.guthix.js5.container.Js5Store
import io.guthix.oldscape.server.net.PacketInboundHandler
import io.guthix.oldscape.server.net.StatusEncoder
import io.guthix.oldscape.server.net.StatusResponse
import io.guthix.oldscape.server.net.js5.Js5Decoder
import io.guthix.oldscape.server.net.js5.Js5Encoder
import io.guthix.oldscape.server.net.js5.Js5Handler
import io.guthix.oldscape.server.net.login.LoginDecoder
import io.guthix.oldscape.server.net.login.LoginHandler
import io.guthix.oldscape.server.net.login.SessionIdEncoder
import io.guthix.oldscape.server.world.World
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPipeline
import java.io.IOException
import java.math.BigInteger
import kotlin.random.Random

class ServiceHandler(
    private val currentRevision: Int,
    private val rsaPrivateKey: BigInteger,
    private val rsaMod: BigInteger,
    private val world: World,
    private val store: Js5Store
) : PacketInboundHandler<ConnectionRequest>() {
    override fun channelRead0(ctx: ChannelHandlerContext, msg: ConnectionRequest) {
        ctx.pipeline().addStatusEncoder()
        when (msg) {
            is GameConnectionRequest -> {
                ctx.write(StatusResponse.SUCCESSFUL)
                ctx.pipeline().replaceSessionIdEncoder()
                val sessionId = Random.nextLong()
                ctx.writeAndFlush(sessionId)
                ctx.pipeline().swapToLogin(world, sessionId, rsaPrivateKey, rsaMod)
            }
            is Js5ConnectionRequest -> {
                if (msg.revision != currentRevision) {
                    ctx.writeAndFlush(StatusResponse.OUT_OF_DATE)
                    throw IOException(
                        "Revision handshake failed, expected revision $currentRevision but got ${msg.revision}."
                    )
                }
                ctx.writeAndFlush(StatusResponse.SUCCESSFUL)
                ctx.pipeline().swapToJs5()
            }
        }
    }

    private fun ChannelPipeline.addStatusEncoder() {
        addAfter(ServiceDecoder::class.qualifiedName, StatusEncoder::class.qualifiedName, StatusEncoder())
    }

    private fun ChannelPipeline.replaceSessionIdEncoder() {
        replace(StatusEncoder::class.qualifiedName, SessionIdEncoder::class.qualifiedName, SessionIdEncoder())
    }

    private fun ChannelPipeline.swapToLogin(
        world: World,
        sessionId: Long,
        rsaPrivateKey: BigInteger,
        rsaMod: BigInteger
    ) {
        replace(ServiceDecoder::class.qualifiedName, LoginDecoder::class.qualifiedName,
            LoginDecoder(store.archiveCount, rsaPrivateKey, rsaMod)
        )
        replace(SessionIdEncoder::class.qualifiedName, StatusEncoder::class.qualifiedName, StatusEncoder())
        replace(ServiceHandler::class.qualifiedName, LoginHandler::class.qualifiedName, LoginHandler(world, sessionId))
    }

    private fun ChannelPipeline.swapToJs5() {
        replace(ServiceDecoder::class.qualifiedName, Js5Decoder::class.qualifiedName, Js5Decoder())
        replace(ServiceHandler::class.qualifiedName, Js5Handler::class.qualifiedName, Js5Handler(store))
        replace(StatusEncoder::class.qualifiedName, Js5Encoder::class.qualifiedName, Js5Encoder())
    }
}