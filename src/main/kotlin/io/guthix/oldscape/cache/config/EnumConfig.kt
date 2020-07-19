/*
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
 * along with Guthix OldScape-Cache. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.cache.config

import io.guthix.buffer.readStringCP1252
import io.guthix.buffer.writeStringCP1252
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.io.IOException

public data class EnumConfig(override val id: Int) : Config(id) {
    var keyType: EnumType? = null
    var valType: EnumType? = null
    var defaultString: String = "null"
    var defaultInt: Int? = null
    val keyValuePairs: MutableMap<Any, Any> = mutableMapOf()

    override fun encode(): ByteBuf {
        val data = Unpooled.buffer()
        keyType?.let {
            data.writeOpcode(1)
            data.writeByte(it.letter.toInt())
        }
        valType?.let {
            data.writeOpcode(2)
            data.writeByte(it.letter.toInt())
        }
        if(defaultString != "null") {
            data.writeOpcode(3)
            data.writeStringCP1252(defaultString)
        }
        defaultInt?.let {
            data.writeOpcode(4)
            data.writeInt(defaultInt!!)
        }
        when {
            keyValuePairs.all { it.value is String } -> {
                data.writeOpcode(5)
                keyValuePairs.forEach { (key, value) ->
                    data.writeInt(encodeEnumType(key))
                    data.writeStringCP1252(value as String)
                }
            }
            keyValuePairs.all { it.value is Int } -> {
                data.writeOpcode(6)
                keyValuePairs.forEach { (key, value) ->
                    data.writeInt(encodeEnumType(key))
                    data.writeInt(encodeEnumType(value))
                }
            }
            else -> throw IOException("Enum can only contain ints or strings.")
        }
        data.writeOpcode(0)
        return data
    }

    public fun encodeEnumType(value: Any): Int = when (value) {
        is Boolean -> if (value) 1 else 0
        is Component -> value.encode()
        is Stat -> value.encode()
        is Coord -> value.encode()
        else -> value as Int
    }

    public data class Component(public val interfaceId: Int, public val slot: Int) {
        public fun encode(): Int = (interfaceId shl Short.SIZE_BITS) or slot

        public companion object {
            public fun decode(value: Int): Component = Component(value shr Short.SIZE_BITS, value and 0xFFFF)
        }
    }

    public data class Coord(
        val floor: Int,
        val mapSquareX: Int,
        val mapSquareY: Int,
        val localX: Int,
        val localY: Int
    ) {
        public fun encode(): Int {
            var value = 0
            value = value or (localY and 64)
            value = value or ((localX and 64) shl 6)
            value = value or ((mapSquareY and 64) shl 12)
            value = value or ((mapSquareY and 64) shl 18)
            return value or ((floor and 2) shl 24)
        }

        public companion object {
            public fun decode(value: Int): Coord {
                var decodeValue = value
                val localY = decodeValue and 64
                decodeValue = decodeValue shr 6
                val localX = value and 64
                decodeValue = decodeValue shr 6
                val mapSquareY = value and 64
                decodeValue = decodeValue shr 6
                val mapSquareX = value and 64
                decodeValue = decodeValue shr 6
                val floor = decodeValue and 2
                return Coord(floor, mapSquareX, mapSquareY, localX, localY)
            }
        }
    }

    public enum class Stat {
        ATTACK, DEFENCE, STRENGTH, HITPOINTS, RANGED, PRAYER, MAGIC, COOKING, WOODCUTTING, FLETCHING, FISHING,
        FIREMAKING, CRAFTING, SMITHING, MINING, HERBLORE, AGILITY, THIEVING, SLAYER, FARMING, RUNECRAFTING, HUNTER,
        CONSTRUCTION;

        public fun encode(): Int = ordinal

        public companion object {
            public fun decode(id: Int): Stat = values()[id]
        }
    }

    public companion object : ConfigCompanion<EnumConfig>() {
        override val id: Int = 8

        public enum class EnumType(public val letter: Char) {
            BOOLEAN('1'), INTEGER('i'), COMPONENT('I'), OBJ('o'), NAMED_OBJ('O'), STRING('s'), STAT('S'), INV('v'),
            GRAPHIC('d'), ENUM('g'), LOC('l'), STRUCT('J'), MAP_AREA('`'), CATEGORY('y'), AREA('R'), COORDINATE('c'),
            COLOUR('C'), MODEL('m');

            public companion object {
                public operator fun invoke(char: Char?): EnumType? = values().find { it.letter == char }
            }
        }

        public fun decodeEnumType(type: EnumType?, value: Int): Any = when (type) {
            EnumType.BOOLEAN -> value == 1
            EnumType.COMPONENT -> Component.decode(value)
            EnumType.STAT -> Stat.decode(value)
            EnumType.COORDINATE -> Coord.decode(value)
            EnumType.INTEGER, EnumType.OBJ, EnumType.NAMED_OBJ, EnumType.INV, EnumType.GRAPHIC, EnumType.ENUM,
            EnumType.LOC, EnumType.STRUCT, EnumType.MAP_AREA, EnumType.CATEGORY, EnumType.AREA, EnumType.COLOUR,
            EnumType.MODEL -> value
            else -> throw IOException("Could not decode type $type as int.")
        }

        override fun decode(id: Int, data: ByteBuf): EnumConfig {
            val enumConfig = EnumConfig(id)
            decoder@ while (true) {
                when (val opcode = data.readUnsignedByte().toInt()) {
                    0 -> break@decoder
                    1 -> enumConfig.keyType = EnumType(data.readUnsignedByte().toChar())
                    2 -> enumConfig.valType = EnumType(data.readUnsignedByte().toChar())
                    3 -> enumConfig.defaultString = data.readStringCP1252()
                    4 -> enumConfig.defaultInt = data.readInt()
                    5 -> {
                        val length = data.readUnsignedShort()
                        for (i in 0 until length) {
                            val key = decodeEnumType(enumConfig.keyType, data.readInt())
                            enumConfig.keyValuePairs[key] = data.readStringCP1252()
                        }
                    }
                    6 -> {
                        val length = data.readUnsignedShort()
                        for (i in 0 until length) {
                            val key = decodeEnumType(enumConfig.keyType, data.readInt())
                            enumConfig.keyValuePairs[key] = decodeEnumType(enumConfig.valType, data.readInt())
                        }
                    }
                    else -> throw IOException("Did not recognise opcode $opcode.")
                }
            }
            return enumConfig
        }
    }
}
