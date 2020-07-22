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