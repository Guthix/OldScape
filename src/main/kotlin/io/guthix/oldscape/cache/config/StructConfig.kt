/*
 * This file is part of Guthix OldScape-Cache.
 *
 * Guthix OldScape-Cache is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Cache is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Guthix OldScape-Cache. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.cache.config

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.io.IOException

public data class StructConfig(override val id: Int) : Config(id) {
    var params: MutableMap<Int, Any>? = null

    override fun encode(): ByteBuf {
        val data = Unpooled.buffer()
        params?.let {
            data.writeOpcode(249)
            data.writeParams(it)
        }
        data.writeOpcode(0)
        return data
    }

    public companion object : ConfigCompanion<StructConfig>() {
        override val id: Int = 34

        @ExperimentalUnsignedTypes
        override fun decode(id: Int, data: ByteBuf): StructConfig {
            val structConfig = StructConfig(id)
            decoder@ while (true) {
                when (val opcode = data.readUnsignedByte().toInt()) {
                    0 -> break@decoder
                    249 -> structConfig.params = data.readParams()
                    else -> throw IOException("Did not recognise opcode $opcode.")
                }
            }
            return structConfig
        }
    }
}