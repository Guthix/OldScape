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
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.net.game.out

import io.guthix.buffer.writeIntIME
import io.guthix.oldscape.server.net.game.FixedSize
import io.guthix.oldscape.server.net.game.OutGameEvent
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class IfMovesubPacket(
    private val fromParentId: Int,
    private val fromSlot: Int,
    private val toParentId: Int,
    private val toSlot: Int
) : OutGameEvent {
    override val opcode: Int = 6

    override val size: FixedSize = FixedSize(STATIC_SIZE)

    override fun encode(ctx: ChannelHandlerContext): ByteBuf {
        val buf = ctx.alloc().buffer(STATIC_SIZE)
        buf.writeIntIME((toParentId shl Short.SIZE_BITS) or toSlot)
        buf.writeInt((fromParentId shl Short.SIZE_BITS) or fromSlot)
        return buf
    }

    companion object {
        const val STATIC_SIZE: Int = Int.SIZE_BYTES + Int.SIZE_BYTES
    }
}