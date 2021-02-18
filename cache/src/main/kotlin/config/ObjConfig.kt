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

data class ObjConfig(override val id: Int) : NamedConfig(id) {
    override var name: String = "null"
    var model: Int = 0
    var zoom2d: Int = 2000
    var xan2d: Int = 0
    var yan2d: Int = 0
    var zan2d: Int = 0
    var xoff2d: Int = 0
    var yoff2d: Int = 0
    var stackable: Boolean = false
    var cost: Int = 1
    var members: Boolean = false
    val groundActions: Array<String?> = arrayOf(null, null, "Take", null, null)
    val iop: Array<String?> = arrayOf(null, null, null, null, "Drop")
    var shiftClickDropIndex: Byte = -2
    var maleModel0: Int? = null
    var maleModel1: Int? = null
    var maleModel2: Int? = null
    var maleOffset: Short = 0
    var femaleModel0: Int? = null
    var femaleModel1: Int? = null
    var femaleModel2: Int? = null
    var femaleOffset: Short = 0
    var maleHeadModel: Int? = null
    var maleHeadModel2: Int? = null
    var femaleHeadModel: Int? = null
    var femaleHeadModel2: Int? = null
    var notedId: Int? = null
    var notedTemplateId: Int? = null
    var resizeX: Int = 128
    var resizeY: Int = 128
    var resizeZ: Int = 128
    var ambient: Byte = 0
    var contrast: Byte = 0
    var team: Short = 0
    var tradable: Boolean = false
    var colorFind: IntArray? = null
    var colorReplace: IntArray? = null
    var textureFind: IntArray? = null
    var textureReplace: IntArray? = null
    var countCo: IntArray? = null
    var countObj: IntArray? = null
    var boughtId: Int? = null
    var boughtTemplate: Int? = null
    var placeholderId: Int? = null
    var placeholderTemplateId: Int? = null
    override var params: MutableMap<Int, Any>? = null
    val isNoted: Boolean get() = notedTemplateId == 799
    val isPlaceHolder: Boolean get() = placeholderTemplateId == 14401

    override fun encode(): ByteBuf {
        val data = Unpooled.buffer()
        if (model != 0) {
            data.writeOpcode(1)
            data.writeShort(model)
        }
        if (name != "null") {
            data.writeOpcode(2)
            data.writeStringCP1252(name)
        }
        if (zoom2d != 2000) {
            data.writeOpcode(4)
            data.writeShort(zoom2d)
        }
        if (xan2d != 0) {
            data.writeOpcode(5)
            data.writeShort(xan2d)
        }
        if (yan2d != 0) {
            data.writeOpcode(6)
            data.writeShort(yan2d)
        }
        if (xoff2d != 0) {
            data.writeOpcode(7)
            if (xoff2d < 0) data.writeShort(xoff2d + 65536) else data.writeShort(xoff2d)
        }
        if (yoff2d != 0) {
            data.writeOpcode(8)
            if (yoff2d < 0) data.writeShort(yoff2d + 65536) else data.writeShort(yoff2d)
        }
        if (stackable) data.writeOpcode(11)
        if (cost != 1) {
            data.writeOpcode(12)
            data.writeInt(cost)
        }
        if (members) data.writeOpcode(16)
        maleModel0?.let {
            data.writeOpcode(23)
            data.writeShort(it)
            data.writeByte(maleOffset.toInt())
        }
        maleModel1?.let {
            data.writeOpcode(24)
            data.writeShort(it)
        }
        femaleModel0?.let {
            data.writeOpcode(25)
            data.writeShort(it)
            data.writeByte(femaleOffset.toInt())
        }
        femaleModel1?.let {
            data.writeOpcode(26)
            data.writeShort(it)
        }
        groundActions.forEachIndexed { i, str ->
            if (str != null && str != "Hidden" && str != "Take") {
                data.writeOpcode(30 + i)
                data.writeStringCP1252(str)
            }
        }
        iop.forEachIndexed { i, str ->
            if (str != null && str != "Hidden" && str != "Drop") {
                data.writeOpcode(35 + i)
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
        if (shiftClickDropIndex.toInt() != -2) {
            data.writeOpcode(42)
            data.writeByte(shiftClickDropIndex.toInt())
        }
        if (tradable) data.writeOpcode(65)
        maleModel2?.let {
            data.writeOpcode(78)
            data.writeShort(it)
        }
        femaleModel2?.let {
            data.writeOpcode(79)
            data.writeShort(it)
        }
        maleHeadModel?.let {
            data.writeOpcode(90)
            data.writeShort(it)
        }
        femaleHeadModel?.let {
            data.writeOpcode(91)
            data.writeShort(it)
        }
        maleHeadModel2?.let {
            data.writeOpcode(92)
            data.writeShort(it)
        }
        femaleHeadModel2?.let {
            data.writeOpcode(93)
            data.writeShort(it)
        }
        if (zan2d != 0) {
            data.writeOpcode(95)
            data.writeShort(zan2d)
        }
        notedId?.let {
            data.writeOpcode(97)
            data.writeShort(it)
        }
        notedTemplateId?.let {
            data.writeOpcode(98)
            data.writeShort(it)
        }
        countObj?.forEachIndexed { i, obj ->
            val countCo = countCo?.get(i) ?: throw IOException("Found OBJ but no CO.")  // ??
            data.writeOpcode(100 + i)
            data.writeShort(obj)
            data.writeShort(countCo)
        }
        if (resizeX != 128) {
            data.writeOpcode(110)
            data.writeShort(resizeX)
        }
        if (resizeY != 128) {
            data.writeOpcode(111)
            data.writeShort(resizeY)
        }
        if (resizeZ != 128) {
            data.writeOpcode(112)
            data.writeShort(resizeZ)
        }
        if (ambient.toInt() != 0) {
            data.writeOpcode(113)
            data.writeByte(ambient.toInt())
        }
        if (contrast.toInt() != 0) {
            data.writeOpcode(114)
            data.writeByte(contrast.toInt())
        }
        if (team.toInt() != 0) {
            data.writeOpcode(115)
            data.writeByte(team.toInt())
        }
        boughtId?.let {
            data.writeOpcode(139)
            data.writeShort(it)
        }
        boughtTemplate?.let {
            data.writeOpcode(140)
            data.writeShort(it)
        }
        placeholderId?.let {
            data.writeOpcode(148)
            data.writeShort(it)
        }
        placeholderTemplateId?.let {
            data.writeOpcode(149)
            data.writeShort(it)
        }
        params?.let {
            data.writeOpcode(249)
            data.writeParams(it)
        }
        data.writeOpcode(0)
        return data
    }

    companion object : NamedConfigCompanion<ObjConfig>() {
        override val id: Int = 10

        override fun decode(id: Int, data: ByteBuf): ObjConfig {
            val objConfig = ObjConfig(id)
            decoder@ while (true) {
                when (val opcode = data.readUnsignedByte().toInt()) {
                    0 -> break@decoder
                    1 -> objConfig.model = data.readUnsignedShort()
                    2 -> objConfig.name = data.readStringCP1252()
                    4 -> objConfig.zoom2d = data.readUnsignedShort()
                    5 -> objConfig.xan2d = data.readUnsignedShort()
                    6 -> objConfig.yan2d = data.readUnsignedShort()
                    7 -> {
                        val temp = data.readUnsignedShort()
                        objConfig.xoff2d = if (temp > 32767) temp - 65536 else temp
                    }
                    8 -> {
                        val temp = data.readUnsignedShort()
                        objConfig.yoff2d = if (temp > 32767) temp - 65536 else temp
                    }
                    11 -> objConfig.stackable = true
                    12 -> objConfig.cost = data.readInt()
                    16 -> objConfig.members = true
                    23 -> {
                        objConfig.maleModel0 = data.readUnsignedShort()
                        objConfig.maleOffset = data.readUnsignedByte()
                    }
                    24 -> objConfig.maleModel1 = data.readUnsignedShort()
                    25 -> {
                        objConfig.femaleModel0 = data.readUnsignedShort()
                        objConfig.femaleOffset = data.readUnsignedByte()
                    }
                    26 -> objConfig.femaleModel1 = data.readUnsignedShort()
                    in 30..34 -> objConfig.groundActions[opcode - 30] = data.readStringCP1252().takeIf {
                        it != "Hidden"
                    }
                    in 35..39 -> objConfig.iop[opcode - 35] = data.readStringCP1252()
                    40 -> {
                        val colorsSize = data.readUnsignedByte().toInt()
                        val colorFind = IntArray(colorsSize)
                        val colorReplace = IntArray(colorsSize)
                        for (i in 0 until colorsSize) {
                            colorFind[i] = data.readUnsignedShort()
                            colorReplace[i] = data.readUnsignedShort()
                        }
                        objConfig.colorFind = colorFind
                        objConfig.colorReplace = colorReplace
                    }
                    41 -> {
                        val texturesSize = data.readUnsignedByte().toInt()
                        val textureFind = IntArray(texturesSize)
                        val textureReplace = IntArray(texturesSize)
                        for (i in 0 until texturesSize) {
                            textureFind[i] = data.readUnsignedShort()
                            textureReplace[i] = data.readUnsignedShort()
                        }
                        objConfig.textureFind = textureFind
                        objConfig.textureReplace = textureReplace
                    }
                    42 -> objConfig.shiftClickDropIndex = data.readByte()
                    65 -> objConfig.tradable = true
                    78 -> objConfig.maleModel2 = data.readUnsignedShort()
                    79 -> objConfig.femaleModel2 = data.readUnsignedShort()
                    90 -> objConfig.maleHeadModel = data.readUnsignedShort()
                    91 -> objConfig.femaleHeadModel = data.readUnsignedShort()
                    92 -> objConfig.maleHeadModel2 = data.readUnsignedShort()
                    93 -> objConfig.femaleHeadModel2 = data.readUnsignedShort()
                    95 -> objConfig.zan2d = data.readUnsignedShort()
                    97 -> objConfig.notedId = data.readUnsignedShort()
                    98 -> objConfig.notedTemplateId = data.readUnsignedShort()
                    in 100..109 -> {
                        if (objConfig.countObj == null) {
                            objConfig.countObj = IntArray(10)
                            objConfig.countCo = IntArray(10)
                        }
                        objConfig.countObj!![opcode - 100] = data.readUnsignedShort()
                        objConfig.countCo!![opcode - 100] = data.readUnsignedShort()
                    }
                    110 -> objConfig.resizeX = data.readUnsignedShort()
                    111 -> objConfig.resizeY = data.readUnsignedShort()
                    112 -> objConfig.resizeZ = data.readUnsignedShort()
                    113 -> objConfig.ambient = data.readByte()
                    114 -> objConfig.contrast = data.readByte()
                    115 -> objConfig.team = data.readUnsignedByte()
                    139 -> objConfig.boughtId = data.readUnsignedShort()
                    140 -> objConfig.boughtTemplate = data.readUnsignedShort()
                    148 -> objConfig.placeholderId = data.readUnsignedShort()
                    149 -> objConfig.placeholderTemplateId = data.readUnsignedShort()
                    249 -> objConfig.params = data.readParams()
                    else -> throw IOException("Did not recognise opcode $opcode.")
                }
            }
            return objConfig
        }
    }
}