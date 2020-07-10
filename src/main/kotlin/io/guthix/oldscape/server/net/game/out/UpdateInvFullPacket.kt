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

import io.guthix.buffer.writeByteNEG
import io.guthix.buffer.writeIntME
import io.guthix.buffer.writeShortLEADD
import io.guthix.oldscape.server.net.game.OutGameEvent
import io.guthix.oldscape.server.net.game.VarShortSize
import io.guthix.oldscape.server.world.entity.Obj
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class UpdateInvFullPacket(
    private val interfaceId: Int,
    private val slotId: Int,
    private val inventoryId: Int,
    private val objs: List<Obj?>
) : OutGameEvent {
    override val opcode: Int = 71

    override val size: VarShortSize = VarShortSize

    override fun encode(ctx: ChannelHandlerContext): ByteBuf {
        val buf = ctx.alloc().buffer()
        buf.writeInt((interfaceId shl 16) or slotId)
        buf.writeShort(inventoryId)
        buf.writeShort(objs.size)
        for (obj in objs) {
            if (obj == null) {
                buf.writeShortLEADD(0)
                buf.writeByteNEG(0)
            } else {
                buf.writeShortLEADD(obj.id + 1)
                if (obj.quantity <= 255) {
                    buf.writeByteNEG(obj.quantity)
                } else {
                    buf.writeByteNEG(255)
                    buf.writeIntME(obj.quantity)
                }
            }
        }
        return buf
    }
}