/**
 * This file is part of Guthix OldScape-Server.
 *
 * Guthix OldScape-Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
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