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
package io.guthix.oldscape.server.net.state.game

import io.guthix.oldscape.server.net.state.IsaacRandom
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import kotlinx.io.IOException

class GameDecoder(private val decodeCipher: IsaacRandom) : ByteToMessageDecoder() {
    private enum class State { OPCODE, SIZE, PAYLOAD }

    private var state = State.OPCODE

    private var decoder: GamePacketDecoder? = null

    private var size: Int = 0

    override fun decode(ctx: ChannelHandlerContext, inc: ByteBuf, out: MutableList<Any>) {
        when(state) {
            State.OPCODE -> {
                if(!inc.isReadable) return
                val opcode = (inc.readUnsignedByte() - decodeCipher.nextInt()) and 0xFF
                println("read opcode $opcode")
                decoder = GamePacketDecoder.inc[opcode] ?: throw IOException(
                    "Could not find packet decoder for opcode $opcode."
                )
                state = State.SIZE
            }
            State.SIZE -> {
                when(decoder?.packetSize ) {
                    is FixedSize -> size = (decoder!!.packetSize as FixedSize).size
                    is VarByteSize -> {
                        if (inc.isReadable) return
                        size = inc.readUnsignedByte().toInt()
                    }
                    is VarShortSize -> {
                        if (!inc.isReadable(Short.SIZE_BYTES)) return
                        size = inc.readUnsignedShort()
                    }
                }
                state = State.PAYLOAD
            }
            State.PAYLOAD -> {
                if(!inc.isReadable(size)) return
                out.add(decoder!!.decode(inc.readBytes(size), ctx))
            }
        }
    }
}