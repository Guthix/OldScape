/*
 * Copyright (C) 2019 Guthix
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.guthix.oldscape.cache.config

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.io.IOException

data class UnderlayConfig(override val id: Int) : Config(id) {
    var color = 0

    override fun encode(): ByteBuf = if(color != 0) {
        Unpooled.buffer(2).apply {
            writeOpcode(1)
            writeMedium(color)
            writeOpcode(0)
        }
    } else {
        Unpooled.buffer(1).apply { writeOpcode(0) }
    }

    companion object : ConfigCompanion<UnderlayConfig>() {
        override val id = 1

        override fun decode(id: Int, data: ByteBuf): UnderlayConfig {
            val underlayConfig = UnderlayConfig(id)
            decoder@ while (true) {
                when (val opcode = data.readUnsignedByte().toInt()) {
                    0 -> break@decoder
                    1 -> underlayConfig.color = data.readUnsignedMedium()
                    else -> throw IOException("Did not recognise opcode $opcode.")
                }
            }
            return underlayConfig
        }
    }
}