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

import io.guthix.buffer.writeIntIME
import io.guthix.buffer.writeIntME
import io.guthix.oldscape.server.net.state.game.FixedSize
import io.guthix.oldscape.server.net.state.game.OutGameEvent
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class IfMovesubPacket(
    private val fromTopInterface: Int,
    private val fromSubInterface: Int,
    private val toTopInterface: Int,
    private val toSubInterface: Int
) : OutGameEvent() {
    override val opcode = 78

    override val size = FixedSize(STATIC_SIZE)

    override fun encode(ctx: ChannelHandlerContext): ByteBuf {
        val buf = ctx.alloc().buffer(STATIC_SIZE)
        buf.writeIntIME((fromTopInterface shl Short.SIZE_BITS) or fromSubInterface)
        buf.writeInt((toTopInterface shl Short.SIZE_BITS) or toSubInterface)
        return buf
    }

    companion object {
        const val STATIC_SIZE = Int.SIZE_BYTES + Int.SIZE_BYTES
    }
}