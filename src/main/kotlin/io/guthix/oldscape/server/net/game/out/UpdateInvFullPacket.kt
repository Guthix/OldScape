/*
 * Copyright 2018-2020 Guthix
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

import io.guthix.buffer.writeByteNeg
import io.guthix.buffer.writeIntME
import io.guthix.buffer.writeShortAddLE
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
                buf.writeShortAddLE(0)
                buf.writeByteNeg(0)
            } else {
                buf.writeShortAddLE(obj.id + 1)
                if (obj.quantity <= 255) {
                    buf.writeByteNeg(obj.quantity)
                } else {
                    buf.writeByteNeg(255)
                    buf.writeIntME(obj.quantity)
                }
            }
        }
        return buf
    }
}