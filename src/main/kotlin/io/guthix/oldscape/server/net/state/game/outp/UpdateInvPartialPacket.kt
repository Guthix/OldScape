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

import io.guthix.buffer.writeSmallSmart
import io.guthix.oldscape.server.net.state.game.GamePacket
import io.guthix.oldscape.server.net.state.game.OutGameEvent
import io.guthix.oldscape.server.net.state.game.VarShortSize
import io.guthix.oldscape.server.world.entity.Obj
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class UpdateInvPartialPacket(
    private val interfaceId: Int,
    private val interfacePosition: Int,
    private val containerId: Int,
    private val objs: Map<Int, Obj?>
) : OutGameEvent {
    override val opcode = 13

    override val size = VarShortSize

    override fun encode(ctx: ChannelHandlerContext): ByteBuf {
        val buf = ctx.alloc().buffer()
        buf.writeInt((interfaceId shl 16) or interfacePosition)
        buf.writeShort(containerId)
        for((slot, obj) in objs) {
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