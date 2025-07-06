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
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.io.IOException

public data class LocConfig(override val id: Int) : NamedConfig(id) {
    override var name: String = "null"
    var width: Short = 1
    var length: Short = 1
    var mapIconId: Int? = null
    val options: Array<String?> = arrayOfNulls(5)
    var clipType: Int = 2
    var isClipped: Boolean = true
    var modelClipped: Boolean = false
    var isHollow: Boolean = false
    var impenetrable: Boolean = true
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
    var contrast: Int = 0
    var mapSceneId: Int? = null
    var modelSizeX: Int = 128
    var modelSizeHeight: Int = 128
    var modelSizeY: Int = 128
    var offsetX: Short = 0
    var offsetHeight: Short = 0
    var offsetY: Short = 0
    var decorDisplacement: Short = 16
    var isMirrored: Boolean = false
    var obstructsGround: Boolean = false
    var nonFlatShading: Boolean = false
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
    override var params: MutableMap<Int, Any>? = null

    override fun encode(): ByteBuf {
        val data = Unpooled.buffer()
        objectModels?.let {
            data.writeOpcode(1)
            data.writeByte(it.size)
            for (i in it.indices) {
                data.writeShort(it[i])
                data.writeByte(it[i])
            }
        }
        if (name != "null") {
            data.writeOpcode(2)
            data.writeStringCP1252(name)
        }
        if (objectTypes == null) {
            objectModels?.let { objectModels ->
                data.writeOpcode(5)
                data.writeByte(objectModels.size)
                for (model in objectModels) {
                    data.writeShort(model)
                }
            }
        }
        if (width.toInt() != 1) {
            data.writeOpcode(14)
            data.writeByte(width.toInt())
        }
        if (length.toInt() != 1) {
            data.writeOpcode(15)
            data.writeByte(length.toInt())
        }
        if (!impenetrable) {
            if (clipType == 0) {
                data.writeOpcode(17)
            } else {
                data.writeOpcode(18)
            }
        }
        anInt2088?.let {
            data.writeOpcode(19)
            data.writeByte(it.toInt())
        }
        contouredGround?.let {
            if (contouredGround == 0) {
                data.writeOpcode(21)
            } else {
                data.writeOpcode(81)
                data.writeByte(it / 256)
            }
        }
        if (nonFlatShading) data.writeOpcode(22)
        if (modelClipped) data.writeOpcode(23)
        if (animationId == null) data.writeShort(65535) else data.writeShort(animationId!!.toInt())
        if (clipType == 1) data.writeOpcode(27)
        if (decorDisplacement.toInt() != 16) {
            data.writeOpcode(28)
            data.writeByte(decorDisplacement.toInt())
        }
        if (ambient.toInt() != 0) {
            data.writeOpcode(29)
            data.writeByte(ambient.toInt())
        }
        if (contrast != 0) {
            data.writeOpcode(39)
            data.writeByte(contrast)
        }
        options.forEachIndexed { i, str ->
            if (str != null && str != "Hidden") {
                data.writeOpcode(30 + i)
                data.writeStringCP1252(str)
            }
        }
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
        if (isMirrored) data.writeOpcode(62)
        if (!isClipped) data.writeOpcode(64)
        if (modelSizeX != 128) {
            data.writeOpcode(65)
            data.writeShort(modelSizeX)
        }
        if (modelSizeHeight != 128) {
            data.writeOpcode(66)
            data.writeShort(modelSizeHeight)
        }
        if (modelSizeY != 128) {
            data.writeOpcode(67)
            data.writeShort(modelSizeY)
        }
        mapSceneId?.let {
            data.writeOpcode(68)
            data.writeShort(it)
        }
        if (accessBlock.toInt() != 0) {
            data.writeOpcode(69)
            data.writeByte(accessBlock.toInt())
        }
        if (offsetX.toInt() != 0) {
            data.writeOpcode(70)
            data.writeShort(offsetX.toInt())
        }
        if (offsetHeight.toInt() != 0) {
            data.writeOpcode(71)
            data.writeShort(offsetHeight.toInt())
        }
        if (offsetY.toInt() != 0) {
            data.writeOpcode(72)
            data.writeShort(offsetY.toInt())
        }
        if (obstructsGround) data.writeOpcode(73)
        if (isHollow) data.writeOpcode(74)
        supportItems?.let {
            data.writeOpcode(75)
            data.writeByte(it.toInt())
        }
        transforms?.let { transforms ->
            data.writeOpcode(if (transforms.last() == null) 118 else 106)
            if (transformVarbit == null) data.writeShort(65535) else data.writeShort(transformVarbit!!.toInt())
            if (transformVarp == null) data.writeShort(65535) else data.writeShort(transformVarp!!.toInt())
            transforms.last()?.let(data::writeShort)
            val size = transforms.size - 2
            data.writeByte(size)
            for (i in 0..size) {
                val transform = transforms[i]
                if (transform == null) {
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

    public companion object : NamedConfigCompanion<LocConfig>() {
        override val id: Int = 6

        override fun decode(id: Int, data: ByteBuf): LocConfig {
            val locConfig = LocConfig(id)
            decoder@ while (true) {
                when (val opcode = data.readUnsignedByte().toInt()) {
                    0 -> break@decoder
                    1 -> {
                        val size = data.readUnsignedByte().toInt()
                        if (size > 0) {
                            locConfig.objectModels = IntArray(size)
                            locConfig.objectTypes = ShortArray(size)
                            for (i in 0 until size) {
                                locConfig.objectModels!![i] = data.readUnsignedShort()
                                locConfig.objectTypes!![i] = data.readUnsignedByte()
                            }
                        }
                    }
                    2 -> locConfig.name = data.readStringCP1252()
                    5 -> {
                        val size = data.readUnsignedByte().toInt()
                        if (size > 0) {
                            locConfig.objectTypes = null
                            locConfig.objectModels = IntArray(size) { data.readUnsignedShort() }
                        }
                    }
                    14 -> locConfig.width = data.readUnsignedByte()
                    15 -> locConfig.length = data.readUnsignedByte()
                    17 -> {
                        locConfig.clipType = 0
                        locConfig.impenetrable = false
                    }
                    18 -> locConfig.impenetrable = false
                    19 -> locConfig.anInt2088 = data.readUnsignedByte()
                    21 -> locConfig.contouredGround = 0
                    22 -> locConfig.nonFlatShading = true
                    23 -> locConfig.modelClipped = true
                    24 -> {
                        locConfig.animationId = data.readUnsignedShort()
                        if (locConfig.animationId!!.toInt() == 65535) {
                            locConfig.animationId = null
                        }
                    }
                    27 -> locConfig.clipType = 1
                    28 -> locConfig.decorDisplacement = data.readUnsignedByte()
                    29 -> locConfig.ambient = data.readByte()
                    39 -> locConfig.contrast = data.readByte().toInt() * 25
                    in 30..34 -> locConfig.options[opcode - 30] = data.readStringCP1252().takeIf { it != "Hidden" }
                    40 -> {
                        val colorsSize = data.readUnsignedByte().toInt()
                        val colorFind = IntArray(colorsSize)
                        val colorReplace = IntArray(colorsSize)
                        for (i in 0 until colorsSize) {
                            colorFind[i] = data.readUnsignedShort()
                            colorReplace[i] = data.readUnsignedShort()
                        }
                        locConfig.colorFind = colorFind
                        locConfig.colorReplace = colorReplace
                    }
                    41 -> {
                        val texturesSize = data.readUnsignedByte().toInt()
                        val textureFind = IntArray(texturesSize)
                        val textureReplace = IntArray(texturesSize)
                        for (i in 0 until texturesSize) {
                            textureFind[i] = data.readUnsignedShort()
                            textureReplace[i] = data.readUnsignedShort()
                        }
                        locConfig.textureFind = textureFind
                        locConfig.textureReplace = textureReplace
                    }
                    62 -> locConfig.isMirrored = true
                    64 -> locConfig.isClipped = false
                    65 -> locConfig.modelSizeX = data.readUnsignedShort()
                    66 -> locConfig.modelSizeHeight = data.readUnsignedShort()
                    67 -> locConfig.modelSizeY = data.readUnsignedShort()
                    68 -> locConfig.mapSceneId = data.readUnsignedShort()
                    69 -> locConfig.accessBlock = data.readUnsignedByte()
                    70 -> locConfig.offsetX = data.readShort()
                    71 -> locConfig.offsetHeight = data.readShort()
                    72 -> locConfig.offsetY = data.readShort()
                    73 -> locConfig.obstructsGround = true
                    74 -> locConfig.isHollow = true
                    75 -> locConfig.supportItems = data.readUnsignedByte()
                    77, 92 -> {
                        val transformVarbit = data.readUnsignedShort()
                        locConfig.transformVarbit = if (transformVarbit == 65535) null else transformVarbit
                        val transformVarp = data.readUnsignedShort()
                        locConfig.transformVarp = if (transformVarbit == 65535) null else transformVarp
                        val lastEntry = if (opcode == 92) {
                            val entry = data.readUnsignedShort()
                            if (entry == 65535) null else entry
                        } else null
                        val size = data.readUnsignedByte().toInt()
                        val transforms = arrayOfNulls<Int?>(size + 2)
                        for (i in 0..size) {
                            val transform = data.readUnsignedShort()
                            transforms[i] = if (transform == 65535) null else transform
                        }
                        if (opcode == 92) {
                            transforms[size + 1] = lastEntry
                        }
                        locConfig.transforms = transforms
                    }
                    78 -> {
                        locConfig.ambientSoundId = data.readUnsignedShort()
                        locConfig.anInt2083 = data.readUnsignedByte()
                    }
                    79 -> {
                        locConfig.anInt2112 = data.readUnsignedShort()
                        locConfig.anInt2113 = data.readUnsignedShort()
                        locConfig.anInt2083 = data.readUnsignedByte()
                        val size = data.readUnsignedByte().toInt()
                        locConfig.anIntArray2084 = IntArray(size) { data.readUnsignedShort() }
                    }
                    81 -> locConfig.contouredGround = data.readUnsignedByte() * 256
                    82 -> locConfig.mapIconId = data.readUnsignedShort()
                    249 -> locConfig.params = data.readParams()
                    else -> throw IOException("Did not recognise opcode $opcode.")
                }
            }
            if (locConfig.anInt2088 == null) {
                locConfig.anInt2088 = 0
                if ((locConfig.objectModels != null && (locConfig.objectTypes == null)
                        || locConfig.objectTypes?.get(0)?.toInt() == 10)
                ) {
                    locConfig.anInt2088 = 1
                }
                for (it in (0 until 5).filter { locConfig.options[it] != null }) {
                    locConfig.anInt2088 = 1
                }
            }
            if (locConfig.supportItems == null) {
                locConfig.supportItems = if (locConfig.clipType != 0) 1 else 0
            }
            if (locConfig.isHollow) {
                locConfig.clipType = 0
                locConfig.impenetrable = false
            }
            return locConfig
        }
    }
}