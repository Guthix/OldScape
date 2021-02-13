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

import io.guthix.buffer.readCharCP1252
import io.guthix.buffer.readStringCP1252
import io.guthix.buffer.writeCharCP1252
import io.guthix.buffer.writeStringCP1252
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.io.IOException

public data class ParamConfig(override val id: Int) : Config(id) {
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
        if (!autoDisable) data.writeOpcode(4)
        defaultString?.let {
            data.writeOpcode(5)
            data.writeStringCP1252(it)
        }
        data.writeOpcode(0)
        return data
    }

    public companion object : ConfigCompanion<ParamConfig>() {
        override val id: Int = 11

        override fun decode(id: Int, data: ByteBuf): ParamConfig {
            val paramConfig = ParamConfig(id)
            decoder@ while (true) {
                when (val opcode = data.readUnsignedByte().toInt()) {
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