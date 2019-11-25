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

import io.guthix.buffer.readStringCP1252
import io.guthix.buffer.writeStringCP1252
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.io.IOException

data class EnumConfig(override val id: Int) : Config(id) {
    var keyType: Char? = null
    var valType: Char? = null
    var defaultString = "null"
    var defaultInt: Int? = null
    val keyValuePairs = mutableMapOf<Int, Any>()

    override fun encode(): ByteBuf {
        val data = Unpooled.buffer()
        keyType?.let {
            data.writeOpcode(1)
            data.writeByte(it.toInt())
        }
        valType?.let {
            data.writeOpcode(2)
            data.writeByte(it.toInt())
        }
        if(defaultString != "null") {
            data.writeOpcode(3)
            data.writeStringCP1252(defaultString)
        }
        defaultInt?.let {
            data.writeOpcode(4)
            data.writeInt(defaultInt!!)
        }
        when {
            keyValuePairs.all { it.value is String } -> {
                data.writeOpcode(5)
                keyValuePairs.forEach { (key, value) ->
                    data.writeInt(key)
                    data.writeStringCP1252(value as String)
                }
            }
            keyValuePairs.all { it.value is Int } -> {
                data.writeOpcode(6)
                keyValuePairs.forEach { (key, value) ->
                    data.writeInt(key)
                    data.writeInt(value as Int)
                }
            }
            else -> throw IOException("Enum can only contain ints or strings.")
        }
        data.writeOpcode(0)
        return data
    }

    companion object : ConfigCompanion<EnumConfig>() {
        override val id = 8

        override fun decode(id: Int, data: ByteBuf): EnumConfig {
            val enumConfig = EnumConfig(id)
            decoder@ while (true) {
                when (val opcode = data.readUnsignedByte().toInt()) {
                    0 -> break@decoder
                    1 -> enumConfig.keyType = data.readUnsignedByte().toChar()
                    2 -> enumConfig.valType = data.readUnsignedByte().toChar()
                    3 -> enumConfig.defaultString = data.readStringCP1252()
                    4 -> enumConfig.defaultInt = data.readInt()
                    5 -> {
                        val length = data.readUnsignedShort()
                        for (i in 0 until length) {
                            val key = data.readInt()
                            enumConfig.keyValuePairs[key] = data.readStringCP1252()
                        }
                    }
                    6 -> {
                        val length = data.readUnsignedShort()
                        for (i in 0 until length) {
                            val key = data.readInt()
                            enumConfig.keyValuePairs[key] = data.readInt()
                        }
                    }
                    else -> throw IOException("Did not recognise opcode $opcode.")
                }
            }
            return enumConfig
        }
    }
}