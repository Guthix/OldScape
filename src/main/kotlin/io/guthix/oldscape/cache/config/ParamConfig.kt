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

import io.guthix.buffer.readCharCP1252
import io.guthix.buffer.readStringCP1252
import io.guthix.buffer.writeCharCP1252
import io.guthix.buffer.writeStringCP1252
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.io.IOException

data class ParamConfig(override val id: Int) : Config(id) {
    var stackType: Char? = null
    var autoDisable: Boolean = true
    var defaultInt: Int? = null
    var defaultString: String? = null

    override fun encode(): ByteBuf {
        val data = Unpooled.buffer()
        stackType?.let {
            data.writeOpcode(1)
            data.writeCharCP1252(it)
        }
        defaultInt?.let {
            data.writeOpcode(2)
            data.writeInt(it)
        }
        if(!autoDisable) data.writeOpcode(4)
        defaultString?.let {
            data.writeOpcode(5)
            data.writeStringCP1252(it)
        }
        data.writeOpcode(0)
        return data
    }

    companion object : ConfigCompanion<ParamConfig>() {
        override val id = 11

        override fun decode(id: Int, data: ByteBuf): ParamConfig {
            val paramConfig = ParamConfig(id)
            decoder@ while (true) {
                when(val opcode = data.readUnsignedByte().toInt()) {
                    0 -> break@decoder
                    1 -> paramConfig.stackType = data.readCharCP1252()
                    2 -> paramConfig.defaultInt = data.readInt()
                    4 -> paramConfig.autoDisable = false
                    5 -> paramConfig.defaultString = data.readStringCP1252()
                    else -> throw IOException("Did not recognise opcode $opcode.")
                }
            }
            return paramConfig
        }
    }
}