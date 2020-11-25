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
package io.guthix.oldscape.server.net.game

import io.guthix.oldscape.server.net.IsaacRandom
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class GameEncoder(private val encodeCipher: IsaacRandom) : MessageToByteEncoder<OutGameEvent>() {
    override fun encode(ctx: ChannelHandlerContext, msg: OutGameEvent, out: ByteBuf) {
        val packet = msg.toPacket(ctx)
        out.writeByte(packet.opcode + encodeCipher.nextInt())
        when (packet.type) {
            VarShortSize -> out.writeShort(packet.payload.readableBytes())
            VarByteSize -> out.writeByte(packet.payload.readableBytes())
            else -> {
            }
        }
        out.writeBytes(packet.payload)
    }
}