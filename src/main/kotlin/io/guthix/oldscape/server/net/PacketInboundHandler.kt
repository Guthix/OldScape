/*
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
 * along with Guthix OldScape-Server. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.net

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

abstract class PacketInboundHandler<P> : SimpleChannelInboundHandler<P>() {
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        if (!cause.message.equals("An existing connection was forcibly closed by the remote host")) {
            logger.error(cause) { "Error while handling message, closing connection." }
        }
        ctx.close()
    }
}