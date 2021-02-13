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
import io.guthix.buffer.writeStringCP1252
import io.guthix.oldscape.server.net.game.OutGameEvent
import io.guthix.oldscape.server.net.game.VarByteSize
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class MessageGamePacket(
    private val type: Int,
    private val isInteractingMessage: Boolean,
    private val message: String
) : OutGameEvent {
    override val opcode: Int = 39

    override val size: VarByteSize = VarByteSize

    override fun encode(ctx: ChannelHandlerContext): ByteBuf {
        val buf = ctx.alloc().buffer(message.length + STATIC_SIZE)
        buf.writeSmallSmart(type)
        buf.writeBoolean(isInteractingMessage)
        buf.writeStringCP1252(message)
        return buf
    }

    companion object {
        const val STATIC_SIZE: Int = Short.SIZE_BYTES + Byte.SIZE_BYTES
    }
}