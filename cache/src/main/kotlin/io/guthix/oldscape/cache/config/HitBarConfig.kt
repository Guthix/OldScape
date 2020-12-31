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
import io.guthix.buffer.writeNullableLargeSmart
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.io.IOException

public data class HitBarConfig(override val id: Int) : Config(id) {
    var int1: Short = 255
    var int2: Short = 255
    var int3: Int? = null
    var int4: Int = 70
    var frontSpriteId: Int? = null
    var backSpriteId: Int? = null
    var width: Short = 30
    var widthPadding: Short = 0

    override fun encode(): ByteBuf {
        val data = Unpooled.buffer()
        if(int1.toInt() != 255) {
            data.writeOpcode(2)
            data.writeByte(int1.toInt())
        }
        if(int2.toInt() != 255) {
            data.writeOpcode(3)
            data.writeByte(int2.toInt())
        }
        int3?.let {
            if(it == 0) {
                data.writeOpcode(4)
            } else {
                data.writeOpcode(11)
                data.writeShort(it)
            }
        }
        if(int4 != 70) {
            data.writeOpcode(5)
            data.writeShort(int4)
        }
        frontSpriteId.let {
            data.writeOpcode(7)
            data.writeNullableLargeSmart(it)
        }
        backSpriteId.let {
            data.writeOpcode(8)
            data.writeNullableLargeSmart(it)
        }
        if(width.toInt() != 30) {
            data.writeOpcode(14)
            data.writeByte(width.toInt())
        }
        if(widthPadding.toInt() != 0) {
            data.writeOpcode(15)
            data.writeByte(widthPadding.toInt())
        }
        data.writeOpcode(0)
        return data
    }

    public companion object : ConfigCompanion<HitBarConfig>() {
        override val id: Int = 33

        override fun decode(id: Int, data: ByteBuf): HitBarConfig {
            val hitBarConfig = HitBarConfig(id)
            decoder@ while (true) {
                when(val opcode = data.readUnsignedByte().toInt()) {
                    0 -> break@decoder
                    1 -> data.readUnsignedShort()
                    2 -> hitBarConfig.int1 = data.readUnsignedByte()
                    3 -> hitBarConfig.int2 = data.readUnsignedByte()
                    4 -> hitBarConfig.int3 = 0
                    5 -> hitBarConfig.int4 = data.readUnsignedShort()
                    6 -> data.readUnsignedByte()
                    7 -> hitBarConfig.frontSpriteId = data.readNullableLargeSmart()
                    8 -> hitBarConfig.backSpriteId = data.readNullableLargeSmart()
                    11 -> hitBarConfig.int3 = data.readUnsignedShort()
                    14 -> hitBarConfig.width = data.readUnsignedByte()
                    15 -> hitBarConfig.widthPadding = data.readUnsignedByte()
                    else -> throw IOException("Did not recognise opcode $opcode.")
                }
            }
            return HitBarConfig(id)
        }

    }
}