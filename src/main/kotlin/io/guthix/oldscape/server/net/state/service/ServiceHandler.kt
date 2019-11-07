/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.net.state.service

import io.guthix.oldscape.server.net.IncPacket
import io.guthix.oldscape.server.net.PacketInboundHandler
import io.guthix.oldscape.server.net.StatusResponse
import io.guthix.oldscape.server.net.StatusEncoder
import io.guthix.oldscape.server.net.state.js5.Js5Decoder
import io.guthix.oldscape.server.net.state.js5.Js5Handler
import io.guthix.oldscape.server.net.state.login.*
import io.guthix.oldscape.server.world.World
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPipeline
import kotlinx.io.IOException
import java.math.BigInteger
import kotlin.random.Random

class ServiceHandler(
    private val currentRevision: Int,
    private val archiveCount: Int,
    private val world: World,
    private val rsaPrivateKey: BigInteger,
    private val rsaMod: BigInteger
) : PacketInboundHandler<IncPacket>() {
    override fun channelRead0(ctx: ChannelHandlerContext, msg: IncPacket) {
        ctx.pipeline().addStatusEncoder()
        when(msg) {
            is GameConnectionRequest -> {
                ctx.write(StatusResponse.SUCCESSFUL)
                ctx.pipeline().replaceSessionIdEncoder()
                val sessionId = Random.nextLong()
                ctx.writeAndFlush(sessionId)
                ctx.pipeline().swapToLogin(world, sessionId, rsaPrivateKey, rsaMod)
            }
            is Js5ConnectionRequest -> {
                if(msg.revision != currentRevision) {
                    ctx.writeAndFlush(StatusResponse.OUT_OF_DATE)
                    throw IOException(
                        "Revision handshake failed, expected revision $currentRevision but got ${msg.revision}."
                    )
                } else {
                    ctx.writeAndFlush(StatusResponse.SUCCESSFUL)
                    ctx.pipeline().swapToJs5()
                }
            }
        }
    }

    private fun ChannelPipeline.addStatusEncoder() {
        addAfter(ServiceDecoder::class.qualifiedName, StatusEncoder::class.qualifiedName, StatusEncoder())
    }

    private fun ChannelPipeline.replaceSessionIdEncoder() {
        replace(StatusEncoder::class.qualifiedName, SessionIdEncoder::class.qualifiedName, SessionIdEncoder())
    }

    private fun ChannelPipeline.swapToLogin(world: World, sessionId: Long, rsaPrivateKey: BigInteger, rsaMod: BigInteger) {
        replace(ServiceDecoder::class.qualifiedName, LoginDecoder::class.qualifiedName,
                LoginDecoder(archiveCount, rsaPrivateKey, rsaMod)
        )
        replace(ServiceHandler::class.qualifiedName, LoginHandler::class.qualifiedName, LoginHandler(world, sessionId))
        replace(SessionIdEncoder::class.qualifiedName, LoginEncoder::class.qualifiedName, LoginEncoder())
    }

    private fun ChannelPipeline.swapToJs5() {
        replace(ServiceDecoder::class.qualifiedName, Js5Decoder::class.qualifiedName, Js5Decoder())
        replace(ServiceHandler::class.qualifiedName, Js5Handler::class.qualifiedName, Js5Handler())
    }
}