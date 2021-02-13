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

import io.guthix.buffer.readNullableLargeSmart
import io.guthix.buffer.readStringCP1252
import io.guthix.buffer.writeNullableLargeSmart
import io.guthix.buffer.writeStringCP1252
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.io.IOException

public class AreaConfig(override val id: Int) : Config(id) {
    public var name: String? = null
    public var spriteId1: Int? = null
    public var spriteId2: Int? = null
    public var field3033: Int? = null
    public var textSize: Short = 0
    public val iop: Array<String?> = arrayOfNulls(5)
    public var shortArray: ShortArray? = null
    public var intArray: IntArray? = null
    public var byteArray: ByteArray? = null
    public var menuTargetName: String? = null
    public var category: Int? = null
    public var horizontalAlignment: Short? = null
    public var verticalAlignment: Short? = null

    override fun encode(): ByteBuf {
        val data = Unpooled.buffer()
        spriteId1.let {
            data.writeOpcode(1)
            data.writeNullableLargeSmart(it)
        }
        spriteId2.let {
            data.writeOpcode(2)
            data.writeNullableLargeSmart(it)
        }
        name?.let {
            data.writeOpcode(3)
            data.writeStringCP1252(it)
        }
        field3033?.let {
            data.writeOpcode(4)
            data.writeMedium(it)
        }
        if (textSize.toInt() != 0) {
            data.writeOpcode(6)
            data.writeByte(textSize.toInt())
        }
        iop.forEachIndexed { i, menuAction ->
            menuAction?.let {
                data.writeOpcode(i + 10)
                data.writeStringCP1252(menuAction)
            }
        }
        shortArray?.let { shortArray ->
            intArray?.let { intArray ->
                byteArray?.let { byteArray ->
                    data.writeOpcode(15)
                    data.writeByte(shortArray.size / 2)
                    shortArray.forEach { data.writeShort(it.toInt()) }
                    data.writeInt(0)
                    data.writeByte(intArray.size)
                    intArray.forEach { data.writeInt(it) }
                    byteArray.forEach { data.writeByte(it.toInt()) }
                }
            }
        }
        menuTargetName?.let {
            data.writeOpcode(17)
            data.writeStringCP1252(it)
        }
        category?.let {
            data.writeOpcode(19)
            data.writeShort(it)
        }
        data.writeOpcode(0)
        return data
    }

    public companion object : ConfigCompanion<AreaConfig>() {
        override val id: Int = 35

        override fun decode(id: Int, data: ByteBuf): AreaConfig {
            val areaConfig = AreaConfig(id)
            decoder@ while (true) {
                when (val opcode = data.readUnsignedByte().toInt()) {
                    0 -> break@decoder
                    1 -> areaConfig.spriteId1 = data.readNullableLargeSmart()
                    2 -> areaConfig.spriteId2 = data.readNullableLargeSmart()
                    3 -> areaConfig.name = data.readStringCP1252()
                    4 -> areaConfig.field3033 = data.readUnsignedMedium()
                    5 -> data.readUnsignedMedium()
                    6 -> areaConfig.textSize = data.readUnsignedByte()
                    7 -> data.readUnsignedByte() // some type of flag set
                    8 -> data.readUnsignedByte()
                    in 10..14 -> areaConfig.iop[opcode - 10] = data.readStringCP1252()
                    15 -> {
                        val size = data.readUnsignedByte().toInt()
                        areaConfig.shortArray = ShortArray(size * 2) {
                            data.readShort()
                        }
                        data.readInt()
                        val size2 = data.readUnsignedByte().toInt()
                        areaConfig.intArray = IntArray(size2) {
                            data.readInt()
                        }
                        areaConfig.byteArray = ByteArray(size2) {
                            data.readByte()
                        }
                    }
                    17 -> areaConfig.menuTargetName = data.readStringCP1252()
                    18 -> data.readNullableLargeSmart()
                    19 -> areaConfig.category = data.readUnsignedShort()
                    21 -> data.readInt()
                    22 -> data.readInt()
                    23 -> repeat(3) { data.readUnsignedByte() }
                    24 -> repeat(2) { data.readShort() }
                    25 -> data.readNullableLargeSmart()
                    28 -> data.readUnsignedByte()
                    29 -> areaConfig.horizontalAlignment = data.readUnsignedByte()
                    30 -> areaConfig.verticalAlignment = data.readUnsignedByte()
                    else -> throw IOException("Did not recognise opcode $opcode.")
                }
            }
            return areaConfig
        }
    }
}