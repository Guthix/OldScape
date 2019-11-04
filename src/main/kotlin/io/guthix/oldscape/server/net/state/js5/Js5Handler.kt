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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.net.state.js5

import io.guthix.oldscape.server.net.PacketInboundHandler
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.DefaultFileRegion
import java.nio.channels.FileChannel
import java.nio.file.Path

class Js5Handler : PacketInboundHandler<Js5FileRequest>() {
    override fun channelRead0(ctx: ChannelHandlerContext, msg: Js5FileRequest) {
        val indexFolder = Path.of(cacheDir + msg.indexFileId)
        val packet = indexFolder.resolve(msg.containerId.toString()).toFile()
        ctx.writeAndFlush(DefaultFileRegion(packet, 0, packet.length()))
    }

    companion object {
        const val cacheDir = "server\\build\\resources\\main\\cache\\"
    }
}