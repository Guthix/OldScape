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
package io.guthix.oldscape.server.net.game.out

import io.guthix.buffer.writeByteADD
import io.guthix.buffer.writeByteNEG
import io.guthix.buffer.writeByteSUB
import io.guthix.buffer.writeShortLEADD
import io.guthix.oldscape.server.dimensions.TileUnit
import io.guthix.oldscape.server.dimensions.TileUnitRange
import io.guthix.oldscape.server.net.game.FixedSize
import io.guthix.oldscape.server.net.game.ZoneOutGameEvent
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class LocPrefetchPacket(
    val playerId: Int,
    val locId: Int,
    val locType: Int,
    val locRotation: Int,
    val xRange: TileUnitRange,
    val yRange: TileUnitRange,
    val cycleRange: IntRange,
    localX: TileUnit,
    localY: TileUnit
) : ZoneOutGameEvent(localX, localY) {
    override val opcode: Int = 76

    override val enclOpcode: Int = 5

    override val size: FixedSize = FixedSize(STATIC_SIZE)

    override fun encode(ctx: ChannelHandlerContext): ByteBuf {
        val buf = ctx.alloc().buffer(STATIC_SIZE)
        buf.writeByteSUB(yRange.first.value)
        buf.writeShortLEADD(cycleRange.last)
        buf.writeShort(playerId)
        buf.writeShortLEADD(locId)
        buf.writeByteNEG((locType shl 2) or (locRotation and 0x3))
        buf.writeByte(xRange.first.value)
        buf.writeByteADD(posBitPack)
        buf.writeByteADD(xRange.last.value)
        buf.writeShortLEADD(cycleRange.first)
        buf.writeByteADD(yRange.last.value)
        return buf
    }

    companion object {
        const val STATIC_SIZE: Int = Short.SIZE_BYTES + Short.SIZE_BYTES + Byte.SIZE_BYTES + Short.SIZE_BYTES +
            Byte.SIZE_BYTES + Byte.SIZE_BYTES + Short.SIZE_BYTES + Byte.SIZE_BYTES + Byte.SIZE_BYTES + Byte.SIZE_BYTES
    }
}