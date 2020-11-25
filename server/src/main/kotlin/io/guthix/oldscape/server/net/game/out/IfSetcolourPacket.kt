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

import io.guthix.oldscape.server.net.game.FixedSize
import io.guthix.oldscape.server.net.game.OutGameEvent
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class IfSetcolourPacket(
    private val rootInterfaceId: Int,
    private val slotId: Int,
    private val red: Int,
    private val green: Int,
    private val blue: Int
) : OutGameEvent {
    override val opcode: Int = 58

    override val size: FixedSize = FixedSize(STATIC_SIZE)

    override fun encode(ctx: ChannelHandlerContext): ByteBuf {
        val buf = ctx.alloc().buffer(STATIC_SIZE)
        buf.writeShortLE((red shl 10) or (green shl 5) or blue)
        buf.writeInt((rootInterfaceId shl 16) or slotId)
        return buf
    }

    companion object {
        const val STATIC_SIZE: Int = Int.SIZE_BYTES + Short.SIZE_BYTES
    }
}