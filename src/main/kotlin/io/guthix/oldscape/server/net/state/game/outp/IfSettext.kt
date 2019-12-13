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

import io.guthix.buffer.writeIntME
import io.guthix.buffer.writeStringCP1252
import io.guthix.oldscape.server.net.state.game.GamePacket
import io.guthix.oldscape.server.net.state.game.OutGameEvent
import io.guthix.oldscape.server.net.state.game.VarShortSize
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class IfSettext(
    private val parentInterface: Int,
    private val slot: Int,
    private val message: String
) : OutGameEvent() {
    override val opcode = 19

    override val size = VarShortSize

    override fun encode(ctx: ChannelHandlerContext): ByteBuf {
        val buf = ctx.alloc().buffer(Int.SIZE_BYTES + message.length)
        buf.writeIntME((parentInterface shl 16) or slot)
        buf.writeStringCP1252(message)
        return buf
    }
}