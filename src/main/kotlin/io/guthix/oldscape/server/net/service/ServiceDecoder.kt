/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.net.service

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import java.io.IOException

class ServiceDecoder : ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext, inc: ByteBuf, out: MutableList<Any>) {
        if(!inc.isReadable) return
        inc.markReaderIndex()
        when(val opcode = inc.readUnsignedByte().toInt()) {
            ServiceType.GAME.opcode -> {
                out.add(GameConnectionRequest())
            }
            ServiceType.JS5.opcode -> {
                if(!inc.isReadable(4)) {
                    inc.resetReaderIndex()
                    return
                }
                out.add(Js5ConnectionRequest(inc.readInt()))
            }
            else -> throw IOException("Could not identify service with opcode $opcode.")
        }
    }
}