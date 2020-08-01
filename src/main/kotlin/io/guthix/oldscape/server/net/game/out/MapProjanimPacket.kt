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

import io.guthix.buffer.writeByteNeg
import io.guthix.buffer.writeByteSub
import io.guthix.buffer.writeShortAdd
import io.guthix.buffer.writeShortAddLE
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
        buf.writeShortAdd(lifespan)
        buf.writeByteSub(endHeight)
        buf.writeByteNeg(startHeight)
        buf.writeByteNeg(angle)
        buf.writeByteNeg(deltaX.value)
        buf.writeShortAddLE(id)
        buf.writeShortLE(delay)
        buf.writeByte(posBitPack)
        buf.writeByte(steepness)
        buf.writeShort(target)
        buf.writeByteNeg(deltaY.value)
        return buf
    }

    companion object {
        const val STATIC_SIZE: Int = 15
    }
}