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
package io.guthix.oldscape.server.net.game.out

import io.guthix.buffer.writeByteADD
import io.guthix.buffer.writeShortLEADD
import io.guthix.oldscape.server.net.game.OutGameEvent
import io.guthix.oldscape.server.net.game.VarShortSize
import io.guthix.oldscape.server.world.entity.Obj
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class UpdateInvFullPacket(
    private val interfaceId: Int,
    private val interfacePosition: Int,
    private val containerId: Int,
    private val objs: List<Obj?>
) : OutGameEvent {
    override val opcode = 71

    override val size = VarShortSize

    override fun encode(ctx: ChannelHandlerContext): ByteBuf {
        val buf = ctx.alloc().buffer()
        buf.writeInt((interfaceId shl 16) or interfacePosition)
        buf.writeShort(containerId)
        buf.writeShort(objs.size)
        for(obj in objs) {
            if(obj == null) {
                buf.writeByteADD(0)
                buf.writeShortLEADD(0)
            } else {
                if(obj.quantity <= 255) {
                    buf.writeByteADD(obj.quantity)
                } else {
                    buf.writeByteADD(255)
                    buf.writeIntLE(obj.quantity)
                }
                buf.writeShortLEADD(obj.id + 1)
            }
        }
        return buf
    }
}