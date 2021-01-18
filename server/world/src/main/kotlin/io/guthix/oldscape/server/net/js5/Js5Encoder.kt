/*
 * Copyright 2018-2021 Guthix
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