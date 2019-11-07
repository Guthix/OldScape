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

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.io.IOException

data class IdentKitConfig(override val id: Int) : Config(id) {
    var colorFind: IntArray? = null
    var colorReplace: IntArray? = null
    var textureFind: IntArray? = null
    var textureReplace: IntArray? = null
    var bodyPartId: Short? = null
    var modelIds: IntArray? = null
    val models = intArrayOf(-1, -1, -1, -1, -1)
    var nonSelectable = false

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
        if(nonSelectable) data.writeOpcode(3)
        colorFind?.let { colorFind -> colorReplace?.let { colorReplace->
            data.writeOpcode(40)
            data.writeByte(colorFind.size)
            for (i in colorFind.indices) {
                data.writeShort(colorFind[i])
                data.writeShort(colorReplace[i])
            }
        } }
        textureFind?.let { textureFind -> textureReplace?.let { textureReplace->
            data.writeOpcode(41)
            data.writeByte(textureFind.size)
            for (i in textureFind.indices) {
                data.writeShort(textureFind[i])
                data.writeShort(textureReplace[i])
            }
        } }
        models.forEachIndexed { i, id ->
            if(id != -1) {
                data.writeOpcode(60 + i)
                data.writeShort(id)
            }
        }
        data.writeOpcode(0)
        return data
    }

    companion object : ConfigCompanion<IdentKitConfig>() {
        override val id = 3

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