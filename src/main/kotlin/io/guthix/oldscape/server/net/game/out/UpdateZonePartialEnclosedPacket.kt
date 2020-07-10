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

import io.guthix.buffer.writeByteSUB
import io.guthix.oldscape.server.dimensions.TileUnit
import io.guthix.oldscape.server.net.game.OutGameEvent
import io.guthix.oldscape.server.net.game.VarShortSize
import io.guthix.oldscape.server.net.game.ZoneOutGameEvent
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext

class UpdateZonePartialEnclosedPacket(
    private val localX: TileUnit,
    private val localY: TileUnit,
    private val packets: List<ZoneOutGameEvent>
) : OutGameEvent {
    override val opcode: Int = 59

    override val size: VarShortSize = VarShortSize

    override fun encode(ctx: ChannelHandlerContext): ByteBuf {
        val buf = Unpooled.compositeBuffer(1 + packets.size * 2)
        val header = ctx.alloc().buffer(STATIC_SIZE)
        header.writeByteSUB(localY.value)
        header.writeByte(localX.value)
        buf.addComponent(true, header)
        packets.forEach { packet ->
            val opcode = ctx.alloc().buffer(1).apply { writeByte(packet.enclOpcode) }
            val payload = packet.encode(ctx)
            buf.addComponents(true, opcode, payload)
        }
        return buf
    }

    companion object {
        const val STATIC_SIZE: Int = Byte.SIZE_BYTES + Byte.SIZE_BYTES
    }
}