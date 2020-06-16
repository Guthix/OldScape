/**
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
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.cache.config

import io.guthix.buffer.readStringCP1252
import io.guthix.buffer.writeStringCP1252
import io.guthix.cache.js5.Js5Group
import io.netty.buffer.ByteBuf

public abstract class NamedConfig(id: Int) : Config(id) {
    public abstract val name: String
}

public abstract class Config(public open val id: Int) {
    public abstract fun encode(): ByteBuf

    protected fun ByteBuf.writeOpcode(opcode: Int): ByteBuf = writeByte(opcode)

    protected fun ByteBuf.writeParams(params: MutableMap<Int, Any>) {
        writeByte(params.size)
        for((key, value) in params) {
            val isString = value is String
            writeByte(if(isString) 1 else 0)
            writeMedium(key)
            if(isString) writeStringCP1252(value as String) else value as Int
        }
    }
}

public abstract class NamedConfigCompanion<out T: NamedConfig> : ConfigCompanion<T>()

public abstract class ConfigCompanion<out T: Config> {
    public abstract val id: Int

    public fun load(group: Js5Group): Map<Int, T> {
        val configs = mutableMapOf<Int, T>()
        group.files.forEach{ (fileId, file) ->
            configs[fileId] = decode(fileId, file.data)
        }
        return configs
    }

    public abstract fun decode(id: Int, data: ByteBuf): T

    protected fun ByteBuf.readParams(): MutableMap<Int, Any> {
        val amount = readUnsignedByte()
        val paramMap = mutableMapOf<Int, Any>()
        for(i in 0 until amount) {
            val isString = readUnsignedByte().toInt() == 1
            val index = readUnsignedMedium()
            val value: Any = if(isString) readStringCP1252() else readInt()
            paramMap[index] = value
        }
        return paramMap
    }
}
