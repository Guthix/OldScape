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
package io.guthix.oldscape.server.net.state.login

import io.guthix.oldscape.server.net.IncPacket
import io.guthix.oldscape.server.net.PacketInboundHandler
import io.netty.channel.ChannelHandlerContext

class LoginHandler(val sessionId: Long) : PacketInboundHandler<IncPacket>() {
    override fun channelRead0(ctx: ChannelHandlerContext?, msg: IncPacket?) {
        println("Received lo0gin request ${msg as LoginRequest}")
    }
}