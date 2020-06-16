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
package io.guthix.oldscape.server.net.game.inc

import io.guthix.oldscape.server.event.CloseModalEvent
import io.guthix.oldscape.server.net.game.ClientEvent
import io.guthix.oldscape.server.net.game.FixedSize
import io.guthix.oldscape.server.net.game.GamePacketDecoder
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class CloseModalPacket : GamePacketDecoder(30, FixedSize(0)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent = CloseModalEvent()
}