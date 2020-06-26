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
                out.add(Js5ContainerRequest(opcode == Js5Type.URGENT_CONTAINER_REQUEST.opcode, indexFileId, containerId))
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