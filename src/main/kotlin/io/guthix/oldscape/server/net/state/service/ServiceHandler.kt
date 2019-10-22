package io.guthix.oldscape.server.net.state.service

import io.guthix.oldscape.server.net.IncPacket
import io.guthix.oldscape.server.net.PacketInboundHandler
import io.guthix.oldscape.server.net.StatusResponse
import io.guthix.oldscape.server.net.StatusEncoder
import io.guthix.oldscape.server.net.state.js5.Js5Decoder
import io.guthix.oldscape.server.net.state.js5.Js5Handler
import io.guthix.oldscape.server.net.state.login.*
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPipeline
import kotlinx.io.IOException
import kotlin.random.Random

class ServiceHandler(private val currentRevision: Int) : PacketInboundHandler<IncPacket>() {
    override fun channelRead0(ctx: ChannelHandlerContext, msg: IncPacket) {
        ctx.pipeline().addStatusEncoder()
        when(msg) {
            is GameConnectionRequest -> {
                ctx.write(StatusResponse.SUCCESSFUL)
                ctx.pipeline().replaceSessionIdEncoder()
                val sessionId = Random.nextLong()
                ctx.writeAndFlush(sessionId)
                ctx.pipeline().swapToLogin(sessionId)
            }
            is Js5ConnectionRequest -> {
                if(msg.revision != currentRevision) {
                    ctx.writeAndFlush(StatusResponse.OUT_OF_DATE)
                    throw IOException(
                        "Revision handshake failed, expected revision $currentRevision but got ${msg.revision}"
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

    private fun ChannelPipeline.swapToLogin(sessionId: Long) {
        replace(ServiceDecoder::class.qualifiedName, LoginDecoder::class.qualifiedName, LoginDecoder())
        replace(ServiceHandler::class.qualifiedName, LoginHandler::class.qualifiedName, LoginHandler(sessionId))
        replace(SessionIdEncoder::class.qualifiedName, LoginEncoder::class.qualifiedName, LoginEncoder())
    }

    private fun ChannelPipeline.swapToJs5() {
        replace(ServiceDecoder::class.qualifiedName, Js5Decoder::class.qualifiedName, Js5Decoder())
        replace(ServiceHandler::class.qualifiedName, Js5Handler::class.qualifiedName, Js5Handler())
    }
}