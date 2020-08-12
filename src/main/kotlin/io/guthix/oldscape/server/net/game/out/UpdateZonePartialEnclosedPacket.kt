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

import io.guthix.buffer.writeByteSub
import io.guthix.oldscape.server.world.map.dim.TileUnit
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
        header.writeByteSub(localY.value)
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