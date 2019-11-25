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

data class InventoryConfig(override val id: Int) : Config(id) {
    var capacity: Int = 0

    override fun encode(): ByteBuf = if(capacity != 0) {
        Unpooled.buffer(4).apply {
            writeOpcode(2)
            writeShort(capacity)
            writeOpcode(0)
        }
    } else {
        Unpooled.buffer(1).apply {
            writeOpcode(0)
        }
    }

    companion object : ConfigCompanion<InventoryConfig>() {
        override val id = 5

        override fun decode(id: Int, data: ByteBuf): InventoryConfig {
            val inventoryConfig = InventoryConfig(id)
            decoder@ while (true) {
                when (val opcode = data.readUnsignedByte().toInt()) {
                    0 -> break@decoder
                    2 -> inventoryConfig.capacity = data.readUnsignedShort()
                    else -> throw IOException("Did not recognise opcode $opcode.")
                }
            }
            return inventoryConfig
        }
    }
}