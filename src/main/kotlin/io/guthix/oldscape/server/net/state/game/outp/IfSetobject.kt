/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.net.state.game.outp

import io.guthix.buffer.writeIntME
import io.guthix.oldscape.server.net.state.game.FixedSize
import io.guthix.oldscape.server.net.state.game.OutGameEvent
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class IfSetobject(
    private val rootInterfaceId: Int,
    private val slotId: Int,
    private val itemId: Int,
    private val amount: Int
) : OutGameEvent {
    override val opcode = 50

    override val size = FixedSize(STATIC_SIZE)

    override fun encode(ctx: ChannelHandlerContext): ByteBuf {
        val buf = ctx.alloc().buffer(STATIC_SIZE)
        buf.writeShort(itemId)
        buf.writeInt(amount)
        buf.writeIntME((rootInterfaceId shl 16) or slotId)
        return buf
    }

    companion object {
        const val STATIC_SIZE = Short.SIZE_BYTES + Int.SIZE_BYTES + Int.SIZE_BYTES
    }
}