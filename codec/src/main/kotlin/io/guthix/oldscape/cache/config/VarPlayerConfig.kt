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

data class VarPlayerConfig(override val id: Int) : Config(id) {
    var type: Int = 0

    override fun encode(): ByteBuf = if(type != 0) {
        Unpooled.buffer(2).apply {
            writeOpcode(5)
            writeShort(type)
            writeOpcode(0)
        }
    } else {
        Unpooled.buffer(1).apply { writeOpcode(0) }
    }

    companion object : ConfigCompanion<VarPlayerConfig>() {
        override val id = 16

        override fun decode(id: Int, data: ByteBuf): VarPlayerConfig {
            val varPlayerConfig = VarPlayerConfig(id)
            decoder@ while (true) {
                when (val opcode = data.readUnsignedByte().toInt()) {
                    0 -> break@decoder
                    5 -> varPlayerConfig.type = data.readUnsignedShort()
                    else -> throw IOException("Did not recognise opcode $opcode.")
                }
            }
            return varPlayerConfig
        }
    }
}