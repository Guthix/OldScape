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
import io.netty.handler.codec.MessageToByteEncoder

class Js5Encoder : MessageToByteEncoder<Js5ContainerResponse>() {
    override fun encode(ctx: ChannelHandlerContext, msg: Js5ContainerResponse, out: ByteBuf) {
        out.writeByte(msg.indexFileId)
        out.writeShort(msg.containerId)
        out.writeByte(msg.compressionType)
        out.writeInt(msg.compressedSize)
        var dataSize = msg.data.readableBytes()
        if (dataSize > BYTES_AFTER_HEADER) {
            dataSize = BYTES_AFTER_HEADER
        }
        out.writeBytes(msg.data.slice(msg.data.readerIndex(), dataSize))
        msg.data.readerIndex(msg.data.readerIndex() + dataSize)
        while (msg.data.readableBytes() > 0) {
            dataSize = msg.data.readableBytes()
            if (dataSize > BYTES_AFTER_BLOCK) {
                dataSize = BYTES_AFTER_BLOCK
            }
            out.writeByte(255)
            out.writeBytes(msg.data.slice(msg.data.readerIndex(), dataSize))
            msg.data.readerIndex(msg.data.readerIndex() + dataSize)
        }
    }

    companion object {
        private const val SECTOR_DATA_SIZE = 512
        private const val BYTES_AFTER_HEADER = SECTOR_DATA_SIZE - 8
        private const val BYTES_AFTER_BLOCK = SECTOR_DATA_SIZE - 1
    }
}