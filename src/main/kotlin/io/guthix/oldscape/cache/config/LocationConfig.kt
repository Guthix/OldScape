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

data class LocationConfig(override val id: Int): NamedConfig(id) {
    override var name = "null"
    var width: Short = 1
    var length: Short = 1
    var mapIconId: Int? = null
    val options = arrayOfNulls<String>(5)
    var clipType = 2
    var isClipped = true
    var modelClipped = false
    var isHollow = false
    var impenetrable = true
    var accessBlock: Short = 0
    var objectModels: IntArray? = null
    var objectTypes: ShortArray? = null
    var colorReplace: IntArray? = null
    var colorFind: IntArray? = null
    var textureFind: IntArray? = null
    var textureReplace: IntArray? = null
    var anInt2088: Short? = null
    var animationId: Int? = null
    var ambient: Byte = 0
    var contrast = 0
    var mapSceneId: Int? = null
    var modelSizeX: Int = 128
    var modelSizeHeight: Int = 128
    var modelSizeY: Int = 128
    var offsetX: Short = 0
    var offsetHeight: Short = 0
    var offsetY: Short = 0
    var decorDisplacement: Short = 16
    var isMirrored = false
    var obstructsGround = false
    var nonFlatShading = false
    var contouredGround: Int? = null
    var supportItems: Short? = null
    var transformVarbit: Int? = null
    var transformVarp: Int? = null
    var transforms: Array<Int?>? = null
    var ambientSoundId: Int? = null
    var anInt2112: Int = 0
    var anInt2113: Int = 0
    var anInt2083: Short = 0
    var anIntArray2084: IntArray? = null
    var params: MutableMap<Int, Any>? = null

    override fun encode(): ByteBuf {
        val data = Unpooled.buffer()
        objectModels?.let {
            data.writeOpcode(1)
            data.writeByte(it.size)
            for(i in it.indices) {
                data.writeShort(it[i])
                data.writeByte(it[i])
            }
        }
        if(name != "null") {
            data.writeOpcode(2)
            data.writeStringCP1252(name)
        }
        if(objectTypes == null) {
            objectModels?.let { objectModels ->
                data.writeOpcode(5)
                data.writeByte(objectModels.size)
                for(model in objectModels) {
                    data.writeShort(model)
                }
            }
        }
        if(width.toInt() != 1) {
            data.writeOpcode(14)
            data.writeByte(width.toInt())
        }
        if(length.toInt() != 1) {
            data.writeOpcode(15)
            data.writeByte(length.toInt())
        }
        if(!impenetrable) {
            if(clipType == 0) {
                data.writeOpcode(17)
            } else {
                data.writeOpcode(18)
            }
        }
        anInt2088?.let {
            data.writeByte(it.toInt())
        }
        contouredGround?.let {
            if(contouredGround == 0) {
                data.writeOpcode(21)
            } else {
                data.writeOpcode(81)
                data.writeByte(it / 256)
            }
        }
        if(nonFlatShading) data.writeOpcode(22)
        if(modelClipped) data.writeOpcode(23)
        if(animationId == null) data.writeShort(65535) else data.writeShort(animationId!!.toInt())
        if(clipType == 1) data.writeOpcode(27)
        if(decorDisplacement.toInt() != 16) {
            data.writeOpcode(28)
            data.writeByte(decorDisplacement.toInt())
        }
        if(ambient.toInt() != 0) {
            data.writeOpcode(29)
            data.writeByte(ambient.toInt())
        }
        if(contrast != 0) {
            data.writeOpcode(39)
            data.writeByte(contrast)
        }
        options.forEachIndexed { i, str ->
            if(str != null && str != "Hidden") {
                data.writeOpcode(30 + i)
                data.writeStringCP1252(str)
            }
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
        if(isMirrored) data.writeOpcode(62)
        if(!isClipped) data.writeOpcode(64)
        if(modelSizeX != 128) {
            data.writeOpcode(65)
            data.writeShort(modelSizeX)
        }
        if(modelSizeHeight != 128) {
            data.writeOpcode(66)
            data.writeShort(modelSizeHeight)
        }
        if(modelSizeY != 128) {
            data.writeOpcode(67)
            data.writeShort(modelSizeY)
        }
        mapSceneId?.let {
            data.writeOpcode(68)
            data.writeShort(it)
        }
        if(accessBlock.toInt() != 0) {
            data.writeOpcode(69)
            data.writeByte(accessBlock.toInt())
        }
        if(offsetX.toInt() != 0) {
            data.writeOpcode(70)
            data.writeShort(offsetX.toInt())
        }
        if(offsetHeight.toInt() != 0) {
            data.writeOpcode(71)
            data.writeShort(offsetHeight.toInt())
        }
        if(offsetY.toInt() != 0) {
            data.writeOpcode(72)
            data.writeShort(offsetY.toInt())
        }
        if(obstructsGround) data.writeOpcode(73)
        if(isHollow) data.writeOpcode(74)
        supportItems?.let {
            data.writeOpcode(75)
            data.writeByte(it.toInt())
        }
        transforms?.let { transforms ->
            data.writeOpcode(if(transforms.last() == null) 118 else 106)
            if(transformVarbit == null) data.writeShort(65535) else data.writeShort(transformVarbit!!.toInt())
            if(transformVarp == null) data.writeShort(65535) else data.writeShort(transformVarp!!.toInt())
            transforms.last()?.let { data.writeShort(it) }
            val size = transforms.size - 2
            data.writeByte(size)
            for(i in 0..size) {
                val transform = transforms[i]
                if(transform == null) {
                    data.writeShort(65535)
                } else {
                    data.writeShort(transform)
                }
            }
        }
        ambientSoundId?.let {
            data.writeOpcode(78)
            data.writeShort(it)
            data.writeByte(anInt2083.toInt())
        }
        anIntArray2084?.let { intArray ->
            data.writeOpcode(79)
            data.writeShort(anInt2112)
            data.writeShort(anInt2113)
            data.writeByte(anInt2083.toInt())
            data.writeByte(intArray.size)
            intArray.forEach { data.writeShort(it) }
        }
        mapIconId?.let {
            data.writeOpcode(82)
            data.writeShort(it)
        }
        params?.let {
            data.writeOpcode(249)
            data.writeParams(it)
        }
        data.writeOpcode(0)
        return data
    }

    companion object : NamedConfigCompanion<LocationConfig>() {
        override val id = 6

        override fun decode(id: Int, data: ByteBuf): LocationConfig {
            val objectConfig = LocationConfig(id)
            decoder@ while (true) {
                when (val opcode = data.readUnsignedByte().toInt()) {
                    0 -> break@decoder
                    1 -> {
                        val size = data.readUnsignedByte().toInt()
                        if (size > 0) {
                            objectConfig.objectModels = IntArray(size)
                            objectConfig.objectTypes = ShortArray(size)
                            for (i in 0 until size) {
                                objectConfig.objectModels!![i] = data.readUnsignedShort()
                                objectConfig.objectTypes!![i] = data.readUnsignedByte()
                            }
                        }
                    }
                    2 -> objectConfig.name = data.readStringCP1252()
                    5 -> {
                        val size = data.readUnsignedByte().toInt()
                        if (size > 0) {
                            objectConfig.objectTypes = null
                            objectConfig.objectModels = IntArray(size) { data.readUnsignedShort() }
                        }
                    }
                    14 -> objectConfig.width = data.readUnsignedByte()
                    15 -> objectConfig.length = data.readUnsignedByte()
                    17 -> {
                        objectConfig.clipType = 0
                        objectConfig.impenetrable = false
                    }
                    18 -> objectConfig.impenetrable = false
                    19 -> objectConfig.anInt2088 = data.readUnsignedByte()
                    21 -> objectConfig.contouredGround = 0
                    22 -> objectConfig.nonFlatShading = true
                    23 -> objectConfig.modelClipped = true
                    24 -> {
                        objectConfig.animationId = data.readUnsignedShort()
                        if(objectConfig.animationId!!.toInt() == 65535) {
                            objectConfig.animationId = null
                        }
                    }
                    27 -> objectConfig.clipType = 1
                    28 -> objectConfig.decorDisplacement = data.readUnsignedByte()
                    29 -> objectConfig.ambient = data.readByte()
                    39 -> objectConfig.contrast = data.readByte().toInt() * 25
                    in 30..34 -> objectConfig.options[opcode - 30] = data.readStringCP1252().takeIf { it != "Hidden" }
                    40 -> {
                        val colorsSize = data.readUnsignedByte().toInt()
                        val colorFind = IntArray(colorsSize)
                        val colorReplace = IntArray(colorsSize)
                        for (i in 0 until colorsSize) {
                            colorFind[i] = data.readUnsignedShort()
                            colorReplace[i] = data.readUnsignedShort()
                        }
                        objectConfig.colorFind = colorFind
                        objectConfig.colorReplace = colorReplace
                    }
                    41 -> {
                        val texturesSize = data.readUnsignedByte().toInt()
                        val textureFind = IntArray(texturesSize)
                        val textureReplace = IntArray(texturesSize)
                        for (i in 0 until texturesSize) {
                            textureFind[i] = data.readUnsignedShort()
                            textureReplace[i] = data.readUnsignedShort()
                        }
                        objectConfig.textureFind = textureFind
                        objectConfig.textureReplace = textureReplace
                    }
                    62 -> objectConfig.isMirrored = true
                    64 -> objectConfig.isClipped = false
                    65 -> objectConfig.modelSizeX = data.readUnsignedShort()
                    66 -> objectConfig.modelSizeHeight = data.readUnsignedShort()
                    67 -> objectConfig.modelSizeY = data.readUnsignedShort()
                    68 -> objectConfig.mapSceneId = data.readUnsignedShort()
                    69 -> objectConfig.accessBlock = data.readUnsignedByte()
                    70 -> objectConfig.offsetX = data.readShort()
                    71 -> objectConfig.offsetHeight = data.readShort()
                    72 -> objectConfig.offsetY = data.readShort()
                    73 -> objectConfig.obstructsGround = true
                    74 -> objectConfig.isHollow = true
                    75 -> objectConfig.supportItems = data.readUnsignedByte()
                    77, 92 -> {
                        val transformVarbit = data.readUnsignedShort()
                        objectConfig.transformVarbit = if(transformVarbit == 65535) null else transformVarbit
                        val transformVarp = data.readUnsignedShort()
                        objectConfig.transformVarp = if(transformVarbit == 65535) null else transformVarp
                        val lastEntry = if(opcode == 92) {
                            val entry = data.readUnsignedShort()
                            if(entry == 65535) null else entry
                        } else null
                        val size = data.readUnsignedByte().toInt()
                        val transforms = arrayOfNulls<Int?>(size + 2)
                        for(i in 0..size) {
                            val transform = data.readUnsignedShort()
                            transforms[i] = if(transform == 65535) null else transform
                        }
                        if(opcode == 92) {
                            transforms[size + 1] = lastEntry
                        }
                        objectConfig.transforms = transforms
                    }
                    78 -> {
                        objectConfig.ambientSoundId = data.readUnsignedShort()
                        objectConfig.anInt2083 = data.readUnsignedByte()
                    }
                    79 -> {
                        objectConfig.anInt2112 = data.readUnsignedShort()
                        objectConfig.anInt2113 = data.readUnsignedShort()
                        objectConfig.anInt2083 = data.readUnsignedByte()
                        val size = data.readUnsignedByte().toInt()
                        objectConfig.anIntArray2084 = IntArray(size) { data.readUnsignedShort() }
                    }
                    81 -> objectConfig.contouredGround = data.readUnsignedByte() * 256
                    82 -> objectConfig.mapIconId = data.readUnsignedShort()
                    249 -> objectConfig.params = data.readParams()
                    else -> throw IOException("Did not recognise opcode $opcode.")
                }
            }
            if (objectConfig.anInt2088 == null) {
                objectConfig.anInt2088 = 0
                if ((objectConfig.objectModels != null && (objectConfig.objectTypes == null)
                            || objectConfig.objectTypes?.get(0)?.toInt() == 10)
                ) {
                    objectConfig.anInt2088 = 1
                }
                for (i in 0 until 5) {
                    if (objectConfig.options[i] != null) {
                        objectConfig.anInt2088 = 1
                    }
                }
            }
            if (objectConfig.supportItems == null) {
                objectConfig.supportItems = if(objectConfig.clipType != 0) 1 else 0
            }
            if (objectConfig.isHollow) {
                objectConfig.clipType = 0
                objectConfig.impenetrable = false
            }
            return objectConfig
        }
    }
}