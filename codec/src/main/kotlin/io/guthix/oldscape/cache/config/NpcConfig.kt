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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.cache.config

import io.guthix.buffer.readStringCP1252
import io.guthix.buffer.writeStringCP1252
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.io.IOException

data class NpcConfig(override val id: Int) : Config(id) {
    var name = "null"
    var size: Short = 1
    var combatLevel: Int? = null
    var isInteractable = true
    var drawMapDot = true
    var isClickable = true
    var rotation: Int = 32
    var headIcon: Int? = null
    val options = arrayOfNulls<String>(5)
    var stanceAnimation: Int? = null
    var walkSequence: Int? = null
    var walkLeftSequence: Int? = null
    var walkRightSequence: Int? = null
    var walkBackSequence: Int? = null
    var turnLeftAnimation: Int? = null
    var turnRightAnimation: Int? = null
    var colorReplace: IntArray? = null
    var colorFind: IntArray? = null
    var textureReplace: IntArray? = null
    var textureFind: IntArray? = null
    var models: IntArray? = null
    var models2: IntArray? = null
    var resizeX: Int = 128
    var resizeY: Int = 128
    var contrast = 0
    var ambient: Byte = 0
    var hasRenderPriority = false
    var transformVarbit: Int? = null
    var transformVarp: Int? = null
    var transforms: Array<Int?>? = null
    var aBool2190 = false
    var params: MutableMap<Int, Any>? = null

    override fun encode(): ByteBuf {
        val data = Unpooled.buffer()
        models?.let { models ->
            data.writeOpcode(1)
            data.writeByte(models.size)
            models.forEach {
                data.writeShort(it)
            }
        }
        if(name != "null") {
            data.writeOpcode(2)
            data.writeStringCP1252(name)
        }
        if(size.toInt() != 1) {
            data.writeOpcode(12)
            data.writeByte(size.toInt())
        }
        stanceAnimation?.let {
            data.writeOpcode(13)
            data.writeShort(it)
        }
        walkSequence?.let {
            if(walkBackSequence == null && walkRightSequence == null && walkLeftSequence == null) {
                data.writeOpcode(14)
                data.writeShort(it)
            }
        }
        turnLeftAnimation?.let {
            data.writeOpcode(15)
            data.writeShort(it)
        }
        turnRightAnimation?.let {
            data.writeOpcode(16)
            data.writeShort(it)
        }
        walkSequence?.let { walkSequence -> walkBackSequence?.let { walkBackSequence ->
            walkLeftSequence?.let { walkLeftSequence -> walkRightSequence?.let { walkRightSequence ->
                data.writeOpcode(17)
                data.writeShort(walkSequence)
                data.writeShort(walkBackSequence)
                data.writeShort(walkLeftSequence)
                data.writeShort(walkRightSequence)
            } }
        } }
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
        models2?.let { models2 ->
            data.writeOpcode(60)
            data.writeByte(models2.size)
            models2.forEach {
                data.writeShort(it)
            }
        }
        if(!drawMapDot) data.writeOpcode(93)
        combatLevel?.let {
            data.writeOpcode(95)
            data.writeShort(it)
        }
        if(resizeX != 128) {
            data.writeOpcode(97)
            data.writeShort(resizeX)
        }
        if(resizeY != 128) {
            data.writeOpcode(98)
            data.writeShort(resizeY)
        }
        if(hasRenderPriority) data.writeOpcode(99)
        if(ambient.toInt() != 0) {
            data.writeOpcode(100)
            data.writeByte(ambient.toInt())
        }
        if(contrast != 0) {
            data.writeOpcode(101)
            data.writeByte(contrast / 5)
        }
        headIcon?.let {
            data.writeOpcode(102)
            data.writeShort(it)
        }
        if(rotation != 32) {
            data.writeOpcode(103)
            data.writeShort(rotation)
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
        if(!isInteractable) data.writeOpcode(107)
        if(!isClickable) data.writeOpcode(109)
        if(aBool2190) data.writeOpcode(111)
        params?.let {
            data.writeOpcode(249)
            data.writeParams(it)
        }
        data.writeOpcode(0)
        return data
    }

    companion object : ConfigCompanion<NpcConfig>() {
        override val id = 9

        override fun decode(id: Int, data: ByteBuf): NpcConfig {
            val npcConfig = NpcConfig(id)
            decoder@ while (true) {
                when (val opcode = data.readUnsignedByte().toInt()) {
                    0 -> break@decoder
                    1 -> {
                        val length = data.readUnsignedByte().toInt()
                        npcConfig.models = IntArray(length) { data.readUnsignedShort() }
                    }
                    2 -> npcConfig.name = data.readStringCP1252()
                    12 -> npcConfig.size = data.readUnsignedByte()
                    13 -> npcConfig.stanceAnimation = data.readUnsignedShort()
                    14 -> npcConfig.walkSequence = data.readUnsignedShort()
                    15 -> npcConfig.turnLeftAnimation = data.readUnsignedShort()
                    16 -> npcConfig.turnRightAnimation = data.readUnsignedShort()
                    17 -> {
                        npcConfig.walkSequence = data.readUnsignedShort()
                        npcConfig.walkBackSequence = data.readUnsignedShort()
                        npcConfig.walkLeftSequence = data.readUnsignedShort()
                        npcConfig.walkRightSequence = data.readUnsignedShort()
                    }
                    in 30..34 -> npcConfig.options[opcode - 30] = data.readStringCP1252().takeIf { it != "Hidden" }
                    40 -> {
                        val colorsSize = data.readUnsignedByte().toInt()
                        val colorFind = IntArray(colorsSize)
                        val colorReplace = IntArray(colorsSize)
                        for (i in 0 until colorsSize) {
                            colorFind[i] = data.readUnsignedShort()
                            colorReplace[i] = data.readUnsignedShort()
                        }
                        npcConfig.colorFind = colorFind
                        npcConfig.colorReplace = colorReplace
                    }
                    41 -> {
                        val texturesSize = data.readUnsignedByte().toInt()
                        val textureFind = IntArray(texturesSize)
                        val textureReplace = IntArray(texturesSize)
                        for (i in 0 until texturesSize) {
                            textureFind[i] = data.readUnsignedShort()
                            textureReplace[i] = data.readUnsignedShort()
                        }
                        npcConfig.textureFind = textureFind
                        npcConfig.textureReplace = textureReplace
                    }
                    60 -> {
                        val length = data.readUnsignedByte().toInt()
                        npcConfig.models2 = IntArray(length) { data.readUnsignedShort() }
                    }
                    93 -> npcConfig.drawMapDot = false
                    95 -> npcConfig.combatLevel = data.readUnsignedShort()
                    97 -> npcConfig.resizeX = data.readUnsignedShort()
                    98 -> npcConfig.resizeY = data.readUnsignedShort()
                    99 -> npcConfig.hasRenderPriority = true
                    100 -> npcConfig.ambient = data.readByte()
                    101 -> npcConfig.contrast = data.readByte() * 5
                    102 -> npcConfig.headIcon = data.readUnsignedShort()
                    103 -> npcConfig.rotation = data.readUnsignedShort()
                    106, 118 -> {
                        val transformVarbit = data.readUnsignedShort()
                        npcConfig.transformVarbit = if(transformVarbit == 65535) null else transformVarbit
                        val transformVarp = data.readUnsignedShort()
                        npcConfig.transformVarp = if(transformVarbit == 65535) null else transformVarp
                        val lastEntry = if(opcode == 118) {
                            val entry = data.readUnsignedShort()
                            if(entry == 65535) null else entry
                        } else null
                        val size = data.readUnsignedByte().toInt()
                        val transforms = arrayOfNulls<Int?>(size + 2)
                        for(i in 0 ..size) {
                            val transform = data.readUnsignedShort()
                            transforms[i] = if(transform == size) null else transform
                        }
                        if(opcode == 118) {
                            transforms[size + 1] = lastEntry
                        }
                        npcConfig.transforms = transforms
                    }
                    107 -> npcConfig.isInteractable = false
                    109 -> npcConfig.isClickable = false
                    111 -> npcConfig.aBool2190 = true
                    249 -> npcConfig.params = data.readParams()
                    else -> throw IOException("Did not recognise opcode $opcode.")
                }
            }
            return npcConfig
        }
    }
}