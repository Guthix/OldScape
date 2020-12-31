/*
 * Copyright 2018-2021 Guthix
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
package io.guthix.oldscape.cache.config

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.awt.Color
import java.io.IOException

public data class OverlayConfig(override val id: Int) : Config(id) {
    var color: Color = Color(0)
    var texture: Short? = null
    var isHidden: Boolean = true
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

    public companion object : ConfigCompanion<OverlayConfig>() {
        override val id: Int = 4

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