/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.cache.config

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.io.IOException

public data class VarbitConfig(override val id: Int) : Config(id) {
    var varpId: Int = 0
    var lsb: Short = 0
    var msb: Short = 0

    override fun encode(): ByteBuf  = if(varpId != 0 && lsb.toInt() != 0 && msb.toInt() != 0) {
        Unpooled.buffer(6).apply {
            writeOpcode(1)
            writeShort(varpId)
            writeByte(lsb.toInt())
            writeByte(msb.toInt())
            writeOpcode(0)
        }
    } else {
        Unpooled.buffer(1).apply { writeOpcode(0) }
    }

    public companion object : ConfigCompanion<VarbitConfig>() {
        override val id: Int = 14

        override fun decode(id: Int, data: ByteBuf): VarbitConfig {
            val varbitConfig = VarbitConfig(id)
            decoder@ while (true) {
                when(val opcode = data.readUnsignedByte().toInt()) {
                    0 -> break@decoder
                    1 -> {
                        varbitConfig.varpId = data.readUnsignedShort()
                        varbitConfig.lsb = data.readUnsignedByte()
                        varbitConfig.msb = data.readUnsignedByte()
                    }
                    else -> throw IOException("Did not recognise opcode $opcode.")
                }
            }
            return varbitConfig
        }
    }
}