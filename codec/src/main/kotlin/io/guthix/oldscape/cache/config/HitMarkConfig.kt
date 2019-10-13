/*
 * Copyright (C) 2019 Guthix
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.guthix.oldscape.cache.config

import io.guthix.buffer.*
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.io.IOException

data class HitMarkConfig(override val id: Int) : Config(id) {
    var fontId: Int? = null
    var textColor = 16777215
    var int1: Int? = null
    var int2: Int? = null
    var int3: Int? = null
    var int4: Int? = null
    var int5: Short = 0
    var string1: String = ""
    var int6: Int = 70
    var int7: Short = 0
    var int8: Int? = null
    var int9: Short? = null
    var int10: Short = 0
    var transformVarbit: Int? = null
    var transformVarp: Int? = null
    var transforms: Array<Int?>? = null

    override fun encode(): ByteBuf {
        val data = Unpooled.buffer()
        fontId.let {
            data.writeOpcode(1)
            data.writeNullableLargeSmart(it)
        }
        if(textColor != 16777215) {
            data.writeOpcode(2)
            data.writeMedium(textColor)
        }
        int1.let {
            data.writeOpcode(3)
            data.writeNullableLargeSmart(int1)
        }
        int2.let {
            data.writeOpcode(4)
            data.writeNullableLargeSmart(int2)
        }
        int3.let {
            data.writeOpcode(5)
            data.writeNullableLargeSmart(int3)
        }
        int4.let {
            data.writeOpcode(6)
            data.writeNullableLargeSmart(int4)
        }
        if(int5.toInt() != 0) {
            data.writeOpcode(7)
            data.writeShort(int5.toInt())
        }
        if(string1 != "") {
            data.writeOpcode(8)
            data.writeString0CP1252(string1)
        }
        if(int6 != 70) {
            data.writeOpcode(9)
            data.writeShort(int6)
        }
        if(int7.toInt() != 0) {
            data.writeOpcode(10)
            data.writeShort(int7.toInt())
        }
        int8?.let {
            if(it == 0) {
                data.writeOpcode(11)
            } else {
                data.writeOpcode(14)
                data.writeShort(it)
            }
        }
        int9?.let {
            data.writeOpcode(12)
            data.writeByte(it.toInt())
        }
        if(int10.toInt() != 0) {
            data.writeOpcode(13)
            data.writeShort(int10.toInt())
        }
        transforms?.let { transforms ->
            data.writeOpcode(if(transforms.last() == null) 18 else 17)
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
        data.writeOpcode(0)
        return data
    }

    companion object : ConfigCompanion<HitMarkConfig>() {
        override val id = 32

        override fun decode(id: Int, data: ByteBuf): HitMarkConfig {
            val hitmarkConfig = HitMarkConfig(id)
            decoder@ while (true) {
                when(val opcode = data.readUnsignedByte().toInt()) {
                    0 -> break@decoder
                    1 -> hitmarkConfig.fontId = data.readNullableLargeSmart()
                    2 -> hitmarkConfig.textColor = data.readUnsignedMedium()
                    3 -> hitmarkConfig.int1 = data.readNullableLargeSmart()
                    4 -> hitmarkConfig.int2 = data.readNullableLargeSmart()
                    5 -> hitmarkConfig.int3 = data.readNullableLargeSmart()
                    6 -> hitmarkConfig.int4 = data.readNullableLargeSmart()
                    7 -> hitmarkConfig.int5 = data.readShort()
                    8 -> hitmarkConfig.string1 = data.readString0CP1252()
                    9 -> hitmarkConfig.int6 = data.readUnsignedShort()
                    10 -> hitmarkConfig.int7 = data.readShort()
                    11 -> hitmarkConfig.int8 = 0
                    12 -> hitmarkConfig.int9 = data.readUnsignedByte()
                    13 -> hitmarkConfig.int10 = data.readShort()
                    14 -> hitmarkConfig.int8 = data.readUnsignedShort()
                    17, 18 -> {
                        val transformVarbit = data.readUnsignedShort()
                        hitmarkConfig.transformVarbit = if(transformVarbit == 65535) null else transformVarbit
                        val transformVarp = data.readUnsignedShort()
                        hitmarkConfig.transformVarp = if(transformVarbit == 65535) null else transformVarp
                        val lastEntry = if(opcode == 18) {
                            val entry = data.readUnsignedShort()
                            if(entry == 65535) null else entry
                        } else null
                        val size = data.readUnsignedByte().toInt()
                        val transforms = arrayOfNulls<Int?>(size + 2)
                        for(i in 0 ..size) {
                            val transform = data.readUnsignedShort()
                            transforms[i] = if(transform == size) null else transform
                        }
                        if(opcode == 18) {
                            transforms[size + 1] = lastEntry
                        }
                        hitmarkConfig.transforms = transforms
                    }
                    else -> throw IOException("Did not recognise opcode $opcode.")
                }
            }
            return hitmarkConfig
        }
    }
}