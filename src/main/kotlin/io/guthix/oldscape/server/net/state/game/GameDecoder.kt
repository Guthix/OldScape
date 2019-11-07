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

    override fun decode(ctx: ChannelHandlerContext, inc: ByteBuf, out: MutableList<Any>) {
        var packetIn: GamePacketInDefinition? = null
        when(state) {
            State.OPCODE -> {
                if(!inc.isReadable) return
                val opcode = inc.readUnsignedByte() - decodeCipher.nextInt()
                val type = GamePacketInDefinition.inc[opcode] ?: throw IOException(
                    "Could not find packet for opcode $opcode."
                )
                packetIn = type.copy()
                state = State.SIZE
            }
            State.SIZE -> {
                when(packetIn?.size) {
                    -2 -> {
                        if (!inc.isReadable(Short.SIZE_BYTES)) return
                        packetIn.size = inc.readUnsignedShort()
                    }
                    -1 -> {
                        if (inc.isReadable) return
                        packetIn.size = inc.readUnsignedByte().toInt()
                    }
                }
                state = State.PAYLOAD
            }
            State.PAYLOAD -> {
                packetIn?.let {
                    if(!inc.isReadable(it.size)) return
                    out.add(it.decoder.decode(inc.readBytes(it.size)))
                }
            }
        }
    }
}