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
import java.awt.Color
import java.io.IOException

data class OverlayConfig(override val id: Int) : Config(id) {
    var color = Color(0)
    var texture: Short? = null
    var isHidden = true
    var otherColor: Color? = null

    override fun encode(): ByteBuf {
        val data = Unpooled.buffer()
        if(color.rgb != 0) {
            data.writeOpcode(1)
            data.writeMedium(color.rgb)
        }
        texture?.let {
            data.writeOpcode(2)
            data.writeByte(it.toInt())
        }
        if(!isHidden) data.writeOpcode(5)
        otherColor?.let {
            data.writeOpcode(7)
            data.writeMedium(it.rgb)
        }
        data.writeOpcode(0)
        return data
    }

    companion object : ConfigCompanion<OverlayConfig>() {
        override val id = 4

        override fun decode(id: Int, data: ByteBuf): OverlayConfig {
            val overlayConfig = OverlayConfig(id)
            decoder@ while (true) {
                when (val opcode = data.readUnsignedByte().toInt()) {
                    0 -> break@decoder
                    1 -> overlayConfig.color = Color(data.readUnsignedMedium())
                    2 -> overlayConfig.texture = data.readUnsignedByte()
                    5 -> overlayConfig.isHidden = false
                    7 -> overlayConfig.otherColor = Color(data.readUnsignedMedium())
                    else -> throw IOException("Did not recognise opcode $opcode.")
                }
            }
            return overlayConfig
        }
    }
}