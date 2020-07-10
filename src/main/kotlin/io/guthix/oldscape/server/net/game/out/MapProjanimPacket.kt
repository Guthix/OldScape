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

import io.guthix.buffer.writeByteNEG
import io.guthix.buffer.writeByteSUB
import io.guthix.buffer.writeShortADD
import io.guthix.buffer.writeShortLEADD
import io.guthix.oldscape.server.dimensions.TileUnit
import io.guthix.oldscape.server.net.game.FixedSize
import io.guthix.oldscape.server.net.game.ZoneOutGameEvent
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class MapProjanimPacket(
    private val id: Int,
    private val startHeight: Int,
    private val endHeight: Int,
    private val target: Int,
    private val angle: Int,
    private val steepness: Int,
    private val delay: Int,
    private val lifespan: Int,
    private val deltaX: TileUnit,
    private val deltaY: TileUnit,
    startLocalX: TileUnit,
    startLocalY: TileUnit
) : ZoneOutGameEvent(startLocalX, startLocalY) {
    override val opcode: Int = 2

    override val enclOpcode: Int = 4

    override val size: FixedSize = FixedSize(STATIC_SIZE)

    override fun encode(ctx: ChannelHandlerContext): ByteBuf {
        val buf = ctx.alloc().buffer(STATIC_SIZE)
        buf.writeShortADD(lifespan)
        buf.writeByteSUB(endHeight)
        buf.writeByteNEG(startHeight)
        buf.writeByteNEG(angle)
        buf.writeByteNEG(deltaX.value)
        buf.writeShortLEADD(id)
        buf.writeShortLE(delay)
        buf.writeByte(posBitPack)
        buf.writeByte(steepness)
        buf.writeShort(target)
        buf.writeByteNEG(deltaY.value)
        return buf
    }

    companion object {
        const val STATIC_SIZE: Int = 15
    }
}