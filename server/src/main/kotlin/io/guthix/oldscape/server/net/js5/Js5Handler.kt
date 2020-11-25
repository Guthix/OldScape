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

import io.guthix.js5.container.Js5Store
import io.guthix.oldscape.server.net.PacketInboundHandler
import io.netty.channel.ChannelHandlerContext

class Js5Handler(private val store: Js5Store) : PacketInboundHandler<Js5ContainerRequest>() {
    override fun channelRead0(ctx: ChannelHandlerContext, msg: Js5ContainerRequest) {
        val data = store.read(msg.indexFileId, msg.containerId).retain()
        val compressionType = data.readUnsignedByte().toInt()
        val compressedSize = data.readInt()
        val response = Js5ContainerResponse(
            msg.indexFileId,
            msg.containerId,
            compressionType,
            compressedSize,
            data.copy()
        )
        ctx.writeAndFlush(response)
    }
}