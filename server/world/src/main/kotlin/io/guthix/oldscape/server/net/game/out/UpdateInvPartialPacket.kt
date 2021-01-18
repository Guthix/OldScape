/*
 * Copyright 2018-2021 Guthix
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
    private val inventoryId: Int,
    private val objs: Map<Int, Obj?>
) : OutGameEvent {
    override val opcode: Int = 35

    override val size: VarShortSize = VarShortSize

    override fun encode(ctx: ChannelHandlerContext): ByteBuf {
        val buf = ctx.alloc().buffer()
        buf.writeInt((interfaceId shl Short.SIZE_BITS) or slotId)
        buf.writeShort(inventoryId)
        for ((slot, obj) in objs) {
            buf.writeSmallSmart(slot)
            if (obj == null) {
                buf.writeShort(0)
            } else {
                buf.writeShort(obj.id + 1)
                if (obj.quantity <= 255) {
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
        const val STATIC_SIZE: Int = Int.SIZE_BYTES + Short.SIZE_BYTES
    }
}