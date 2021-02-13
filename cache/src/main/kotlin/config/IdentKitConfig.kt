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

public data class IdentKitConfig(override val id: Int) : Config(id) {
    var colorFind: IntArray? = null
    var colorReplace: IntArray? = null
    var textureFind: IntArray? = null
    var textureReplace: IntArray? = null
    var bodyPartId: Short? = null
    var modelIds: IntArray? = null
    val models: IntArray = intArrayOf(-1, -1, -1, -1, -1)
    var nonSelectable: Boolean = false

    @ExperimentalUnsignedTypes
    override fun encode(): ByteBuf {
        val data = Unpooled.buffer()
        bodyPartId?.let {
            data.writeOpcode(1)
            data.writeByte(it.toInt())
        }
        modelIds?.let {
            data.writeOpcode(2)
            data.writeByte(it.size)
            it.forEach { id -> data.writeShort(id) }
        }
        if (nonSelectable) data.writeOpcode(3)
        colorFind?.let { colorFind ->
            colorReplace?.let { colorReplace ->
                data.writeOpcode(40)
                data.writeByte(colorFind.size)
                for (i in colorFind.indices) {
                    data.writeShort(colorFind[i])
                    data.writeShort(colorReplace[i])
                }
            }
        }
        textureFind?.let { textureFind ->
            textureReplace?.let { textureReplace ->
                data.writeOpcode(41)
                data.writeByte(textureFind.size)
                for (i in textureFind.indices) {
                    data.writeShort(textureFind[i])
                    data.writeShort(textureReplace[i])
                }
            }
        }
        models.forEachIndexed { i, id ->
            if (id != -1) {
                data.writeOpcode(60 + i)
                data.writeShort(id)
            }
        }
        data.writeOpcode(0)
        return data
    }

    public companion object : ConfigCompanion<IdentKitConfig>() {
        override val id: Int = 3

        override fun decode(id: Int, data: ByteBuf): IdentKitConfig {
            val identKitConfig = IdentKitConfig(id)
            decoder@ while (true) {
                when (val opcode = data.readUnsignedByte().toInt()) {
                    0 -> break@decoder
                    1 -> identKitConfig.bodyPartId = data.readUnsignedByte()
                    2 -> {
                        val length = data.readUnsignedByte().toInt()
                        identKitConfig.modelIds = IntArray(length) { data.readUnsignedShort() }
                    }
                    3 -> identKitConfig.nonSelectable = true
                    40 -> {
                        val colorsSize = data.readUnsignedByte().toInt()
                        val colorFind = IntArray(colorsSize)
                        val colorReplace = IntArray(colorsSize)
                        for (i in 0 until colorsSize) {
                            colorFind[i] = data.readUnsignedShort()
                            colorReplace[i] = data.readUnsignedShort()
                        }
                        identKitConfig.colorFind = colorFind
                        identKitConfig.colorReplace = colorReplace
                    }
                    41 -> {
                        val texturesSize = data.readUnsignedByte().toInt()
                        val textureFind = IntArray(texturesSize)
                        val textureReplace = IntArray(texturesSize)
                        for (i in 0 until texturesSize) {
                            textureFind[i] = data.readUnsignedShort()
                            textureReplace[i] = data.readUnsignedShort()
                        }
                        identKitConfig.textureFind = textureFind
                        identKitConfig.textureReplace = textureReplace
                    }
                    in 60..69 -> {
                        identKitConfig.models[opcode - 60] = data.readUnsignedShort()
                    }
                    else -> throw IOException("Did not recognise opcode $opcode.")
                }
            }
            return identKitConfig
        }
    }
}