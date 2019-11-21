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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.net.state.game.outp

import io.guthix.buffer.writeByteSUB
import io.guthix.oldscape.server.net.state.game.FixedSize
import io.guthix.oldscape.server.net.state.game.GamePacket
import io.guthix.oldscape.server.net.state.game.OutGameEvent
import io.netty.channel.ChannelHandlerContext

class IfOpensubPacket(
    private val rootInterfaceId: Int,
    private val rootSlotId: Int,
    private val childInterfaceId: Int,
    private val isClickable: Boolean
) : OutGameEvent {
    override fun encode(ctx: ChannelHandlerContext): GamePacket {
        val buf = ctx.alloc().buffer(STATIC_SIZE)
        buf.writeShortLE(childInterfaceId)
        buf.writeByteSUB(if(isClickable) 1 else 0)
        buf.writeInt((rootInterfaceId shl 16) or rootSlotId)
        return GamePacket(52, FixedSize(STATIC_SIZE), buf)
    }

    companion object {
        const val STATIC_SIZE = Short.SIZE_BYTES + Byte.SIZE_BYTES + Int.SIZE_BYTES
    }
}