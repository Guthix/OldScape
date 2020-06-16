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
package io.guthix.oldscape.server.net.game.out

import io.guthix.buffer.writeByteADD
import io.guthix.buffer.writeByteNEG
import io.guthix.buffer.writeStringCP1252
import io.guthix.oldscape.server.net.game.OutGameEvent
import io.guthix.oldscape.server.net.game.VarByteSize
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class SetPlayerOpPacket(val prioritized: Boolean, val slot: Int, val text: String) : OutGameEvent {
    override val opcode: Int = 44

    override val size: VarByteSize = VarByteSize

    override fun encode(ctx: ChannelHandlerContext): ByteBuf {
        val buf = ctx.alloc().buffer(STATIC_SIZE + text.length)
        buf.writeStringCP1252(text)
        buf.writeByteNEG(if (prioritized) 1 else 0)
        buf.writeByteADD(slot)
        return buf
    }

    companion object {
        const val STATIC_SIZE: Int = Byte.SIZE_BYTES + Byte.SIZE_BYTES
    }
}