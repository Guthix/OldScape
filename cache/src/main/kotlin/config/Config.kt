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

import io.guthix.buffer.readStringCP1252
import io.guthix.buffer.writeStringCP1252
import io.guthix.js5.Js5Group
import io.netty.buffer.ByteBuf

public abstract class NamedConfig(id: Int) : Config(id) {
    public abstract val name: String
    public abstract val params: MutableMap<Int, Any>?
}

public abstract class Config(public open val id: Int) {
    public abstract fun encode(): ByteBuf

    protected fun ByteBuf.writeOpcode(opcode: Int): ByteBuf = writeByte(opcode)

    protected fun ByteBuf.writeParams(params: MutableMap<Int, Any>) {
        writeByte(params.size)
        for ((key, value) in params) {
            val isString = value is String
            writeByte(if (isString) 1 else 0)
            writeMedium(key)
            if (isString) writeStringCP1252(value as String) else writeInt(value as Int)
        }
    }
}

public abstract class NamedConfigCompanion<out T : NamedConfig> : ConfigCompanion<T>()

public abstract class ConfigCompanion<out T : Config> {
    public abstract val id: Int

    public fun load(group: Js5Group): Map<Int, T> {
        val configs = mutableMapOf<Int, T>()
        group.files.forEach { (fileId, file) ->
            configs[fileId] = decode(fileId, file.data)
        }
        return configs
    }

    @ExperimentalUnsignedTypes
    public abstract fun decode(id: Int, data: ByteBuf): T

    protected fun ByteBuf.readParams(): MutableMap<Int, Any> {
        val amount = readUnsignedByte()
        val paramMap = mutableMapOf<Int, Any>()
        for (i in 0 until amount) {
            val isString = readUnsignedByte().toInt() == 1
            val index = readUnsignedMedium()
            val value: Any = if (isString) readStringCP1252() else readInt()
            paramMap[index] = value
        }
        return paramMap
    }
}
