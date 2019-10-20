package io.guthix.oldscape.server.net.state.js5

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import kotlinx.io.IOException

class Js5Decoder : ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext, inc: ByteBuf, out: MutableList<Any>) {
        if (!inc.isReadable(4)) return
        when (val opcode = inc.readUnsignedByte().toInt()) {
            Js5Type.NORMAL_CONTAINER_REQUEST.opcode, Js5Type.PRIORITY_CONTAINER_REQUEST.opcode -> {
                val indexFileId = inc.readUnsignedByte().toInt()
                val containerId = inc.readUnsignedShort()
                out.add(Js5FileRequest(opcode == Js5Type.PRIORITY_CONTAINER_REQUEST.opcode, indexFileId, containerId))
            }
            Js5Type.CLIENT_LOGGED_IN.opcode, Js5Type.CLIENT_LOGGED_OUT.opcode -> {
                val statusCode = inc.readUnsignedMedium()
                if (statusCode != 0) {
                    throw IOException("Js5 client status code expected: 0 but was $statusCode.")
                }
            }
            Js5Type.ENCRYPTION_KEY_UPDATE.opcode -> {
                inc.skipBytes(3)
            }

            else -> throw IOException("Could not identify js5 request with opcode $opcode.")
        }
    }
}