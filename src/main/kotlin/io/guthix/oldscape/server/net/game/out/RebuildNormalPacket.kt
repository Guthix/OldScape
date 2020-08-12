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

import io.guthix.cache.js5.util.XTEA_KEY_SIZE
import io.guthix.oldscape.server.world.map.dim.ZoneUnit
import io.guthix.oldscape.server.net.game.OutGameEvent
import io.guthix.oldscape.server.net.game.VarShortSize
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class RebuildNormalPacket(
    private val xteas: List<IntArray>,
    private val x: ZoneUnit,
    private val y: ZoneUnit
) : OutGameEvent {
    override val opcode: Int = 17

    override val size: VarShortSize = VarShortSize

    override fun encode(ctx: ChannelHandlerContext): ByteBuf {
        val buf = ctx.alloc().buffer(STATIC_SIZE + xteas.size * XTEA_KEY_SIZE * Int.SIZE_BYTES)
        buf.writeShort(y.value)
        buf.writeShortLE(x.value)
        buf.writeShort(xteas.size)
        xteas.forEach { xteaKey ->
            xteaKey.forEach { keyPart ->
                buf.writeInt(keyPart)
            }
        }
        return buf
    }

    companion object {
        const val STATIC_SIZE: Int = Short.SIZE_BYTES + Short.SIZE_BYTES + Short.SIZE_BYTES
    }
}