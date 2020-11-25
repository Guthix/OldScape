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
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Player
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import java.io.IOException

class GameDecoder(
    private val decodeCipher: IsaacRandom,
    private val player: Player,
    private val world: World
) : ByteToMessageDecoder() {
    private enum class State { OPCODE, SIZE, PAYLOAD }

    private var state = State.OPCODE

    private var decoder: GamePacketDecoder? = null

    private var size: Int = 0

    override fun decode(ctx: ChannelHandlerContext, inc: ByteBuf, out: MutableList<Any>) {
        if (state == State.OPCODE) {
            if (!inc.isReadable) return
            val opcode = (inc.readUnsignedByte() - decodeCipher.nextInt()) and 0xFF
            decoder = GamePacketDecoder.inc[opcode] ?: throw IOException(
                "Could not find packet decoder for opcode $opcode."
            )
            state = State.SIZE
        }
        if (state == State.SIZE) {
            size = when (decoder!!.packetSize) {
                is FixedSize -> (decoder!!.packetSize as FixedSize).size
                is VarByteSize -> {
                    if (!inc.isReadable) return
                    inc.readUnsignedByte().toInt()
                }
                is VarShortSize -> {
                    if (!inc.isReadable(Short.SIZE_BYTES)) return
                    inc.readUnsignedShort()
                }
            }
            state = State.PAYLOAD
        }
        if (state == State.PAYLOAD) {
            if (!inc.isReadable(size)) return
            val payload = if (size > 0) inc.readBytes(size) else Unpooled.EMPTY_BUFFER
            out.add(decoder!!.decode(payload, size, ctx, player, world))
            state = State.OPCODE
        }
    }
}