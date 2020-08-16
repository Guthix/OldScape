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
package io.guthix.oldscape.server.net.game.out

import io.guthix.buffer.writeByteAdd
import io.guthix.buffer.writeByteNeg
import io.guthix.buffer.writeByteSub
import io.guthix.buffer.writeShortAddLE
import io.guthix.oldscape.server.net.game.FixedSize
import io.guthix.oldscape.server.net.game.ZoneOutGameEvent
import io.guthix.oldscape.server.world.map.dim.TileUnit
import io.guthix.oldscape.server.world.map.dim.TileUnitRange
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
    override val opcode: Int = 33

    override val enclOpcode: Int = 0

    override val size: FixedSize = FixedSize(STATIC_SIZE)

    override fun encode(ctx: ChannelHandlerContext): ByteBuf {
        val buf = ctx.alloc().buffer(STATIC_SIZE)
        buf.writeByteSub(yRange.first.value)
        buf.writeShortAddLE(cycleRange.last)
        buf.writeShort(playerId)
        buf.writeShortAddLE(locId)
        buf.writeByteNeg((locType shl 2) or (locRotation and 0x3))
        buf.writeByte(xRange.first.value)
        buf.writeByteAdd(posBitPack)
        buf.writeByteAdd(xRange.last.value)
        buf.writeShortAddLE(cycleRange.first)
        buf.writeByteAdd(yRange.last.value)
        return buf
    }

    companion object {
        const val STATIC_SIZE: Int = Short.SIZE_BYTES + Short.SIZE_BYTES + Byte.SIZE_BYTES + Short.SIZE_BYTES +
            Byte.SIZE_BYTES + Byte.SIZE_BYTES + Short.SIZE_BYTES + Byte.SIZE_BYTES + Byte.SIZE_BYTES + Byte.SIZE_BYTES
    }
}