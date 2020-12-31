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
import java.io.IOException

public data class UnderlayConfig(override val id: Int) : Config(id) {
    var color: Int = 0

    override fun encode(): ByteBuf = if(color != 0) {
        Unpooled.buffer(2).apply {
            writeOpcode(1)
            writeMedium(color)
            writeOpcode(0)
        }
    } else {
        Unpooled.buffer(1).apply { writeOpcode(0) }
    }

    public companion object : ConfigCompanion<UnderlayConfig>() {
        override val id: Int = 1

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