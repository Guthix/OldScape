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
package io.guthix.oldscape.server.net.js5

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import java.io.IOException

class Js5Decoder : ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext, inc: ByteBuf, out: MutableList<Any>) {
        if (!inc.isReadable(4)) return
        when (val opcode = inc.readUnsignedByte().toInt()) {
            Js5Type.NORMAL_CONTAINER_REQUEST.opcode, Js5Type.URGENT_CONTAINER_REQUEST.opcode -> {
                val indexFileId = inc.readUnsignedByte().toInt()
                val containerId = inc.readUnsignedShort()
                out.add(Js5ContainerRequest(
                    opcode == Js5Type.URGENT_CONTAINER_REQUEST.opcode,
                    indexFileId,
                    containerId
                ))
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