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

import io.guthix.buffer.writeSmallSmart
import io.guthix.oldscape.server.net.game.OutGameEvent
import io.guthix.oldscape.server.net.game.VarShortSize
import io.guthix.oldscape.server.world.entity.Obj
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class UpdateInvPartialPacket(
    private val interfaceId: Int,
    private val slotId: Int,
    private val subInterfaceId: Int,
    private val objs: Map<Int, Obj?>
) : OutGameEvent {
    override val opcode = 13

    override val size = VarShortSize

    override fun encode(ctx: ChannelHandlerContext): ByteBuf {
        val buf = ctx.alloc().buffer()
        buf.writeInt((interfaceId shl Short.SIZE_BITS) or slotId)
        buf.writeShort(subInterfaceId)
        for((slot, obj) in objs) {
            println("$interfaceId, $subInterfaceId, $slotId, ${obj?.quantity}")
            buf.writeSmallSmart(slot)
            if(obj == null) {
                buf.writeShort(0)
            } else {
                buf.writeShort(obj.blueprint.id + 1)
                if(obj.quantity <= 255) {
                    buf.writeByte(obj.quantity)
                } else {
                    buf.writeByte(255)
                    buf.writeInt(obj.quantity)
                }
            }
        }
        return buf
    }

    companion object {
        const val STATIC_SIZE = Int.SIZE_BYTES + Short.SIZE_BYTES
    }
}