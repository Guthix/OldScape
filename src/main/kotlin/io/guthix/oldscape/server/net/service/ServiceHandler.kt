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
package io.guthix.oldscape.server.net.service

import io.guthix.cache.js5.container.Js5Store
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