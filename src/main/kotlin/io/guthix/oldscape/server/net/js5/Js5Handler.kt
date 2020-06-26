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

import io.guthix.cache.js5.container.Js5Store
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