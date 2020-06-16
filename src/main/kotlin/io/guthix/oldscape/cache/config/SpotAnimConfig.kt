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

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.io.IOException

public data class SpotAnimConfig(override val id: Int) : Config(id) {
    var sequenceId: Int? = null
    var rotation: Int = 0
    var resizeY: Int = 128
    var resizeX: Int = 128
    var modelId: Int = 0
    var ambient: Short = 0
    var contrast: Short = 0
    var textureReplace: IntArray? = null
    var textureFind: IntArray? = null
    var colorFind: IntArray? = null
    var colorReplace: IntArray? = null

    override fun encode(): ByteBuf {
        val data = Unpooled.buffer()
        if(modelId != 0) {
            data.writeOpcode(1)
            data.writeShort(modelId)
        }
        sequenceId?.let {
            data.writeOpcode(2)
            data.writeShort(sequenceId!!.toInt())
        }
        if(resizeX != 128) {
            data.writeOpcode(4)
            data.writeByte(resizeX)
        }
        if(resizeY != 128) {
            data.writeOpcode(5)
            data.writeByte(resizeY)
        }
        if(rotation != 0) {
            data.writeOpcode(6)
            data.writeShort(rotation)
        }
        if(ambient.toInt() != 0) {
            data.writeOpcode(7)
            data.writeByte(ambient.toInt())
        }
        if(contrast.toInt() != 0) {
            data.writeOpcode(8)
            data.writeByte(contrast.toInt())
        }
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
        data.writeOpcode(0)
        return data
    }

    public companion object : ConfigCompanion<SpotAnimConfig>() {
        override val id: Int = 13

        override fun decode(id: Int, data: ByteBuf): SpotAnimConfig {
            val spotAnimConfig = SpotAnimConfig(id)
            decoder@ while (true) {
                when (val opcode = data.readUnsignedByte().toInt()) {
                    0 -> break@decoder
                    1 -> spotAnimConfig.modelId = data.readUnsignedShort()
                    2 -> spotAnimConfig.sequenceId = data.readUnsignedShort()
                    4 -> spotAnimConfig.resizeX = data.readUnsignedShort()
                    5 -> spotAnimConfig.resizeY = data.readUnsignedShort()
                    6 -> spotAnimConfig.rotation = data.readUnsignedShort()
                    7 -> spotAnimConfig.ambient = data.readUnsignedByte()
                    8 -> spotAnimConfig.contrast = data.readUnsignedByte()
                    40 -> {
                        val colorsSize = data.readUnsignedByte().toInt()
                        val colorFind = IntArray(colorsSize)
                        val colorReplace = IntArray(colorsSize)
                        for (i in 0 until colorsSize) {
                            colorFind[i] = data.readUnsignedShort()
                            colorReplace[i] = data.readUnsignedShort()
                        }
                        spotAnimConfig.colorFind = colorFind
                        spotAnimConfig.colorReplace = colorReplace
                    }
                    41 -> {
                        val texturesSize = data.readUnsignedByte().toInt()
                        val textureFind = IntArray(texturesSize)
                        val textureReplace = IntArray(texturesSize)
                        for (i in 0 until texturesSize) {
                            textureFind[i] = data.readUnsignedShort()
                            textureReplace[i] = data.readUnsignedShort()
                        }
                        spotAnimConfig.textureFind = textureFind
                        spotAnimConfig.textureReplace = textureReplace
                    }
                    else -> throw IOException("Did not recognise opcode $opcode.")
                }
            }
            return spotAnimConfig
        }
    }
}