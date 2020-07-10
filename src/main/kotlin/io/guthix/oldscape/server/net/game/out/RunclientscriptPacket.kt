/*
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
package io.guthix.oldscape.server.net.game.out

import io.guthix.buffer.writeStringCP1252
import io.guthix.oldscape.server.net.game.OutGameEvent
import io.guthix.oldscape.server.net.game.VarShortSize
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class RunclientscriptPacket(private val id: Int, vararg val params: Any) : OutGameEvent {
    override val opcode: Int = 49

    override val size: VarShortSize = VarShortSize

    override fun encode(ctx: ChannelHandlerContext): ByteBuf {
        val buf = ctx.alloc().buffer()
        val argumentListIdentifier = StringBuilder()
        for (param in params.reversed()) {
            if (param is String) {
                argumentListIdentifier.append("s")
            } else {
                argumentListIdentifier.append("i")
            }
        }
        buf.writeStringCP1252("$argumentListIdentifier")
        for (param in params) {
            if (param is String) {
                buf.writeStringCP1252(param)
            } else {
                buf.writeInt(param as Int)
            }
        }
        buf.writeInt(id)
        return buf
    }
}