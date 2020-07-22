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