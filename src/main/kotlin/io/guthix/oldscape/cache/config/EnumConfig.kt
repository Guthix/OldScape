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

public data class EnumConfig<K, V>(
    override val id: Int,
    val keyValuePairs: MutableMap<K, V> = mutableMapOf()
) : Config(id), MutableMap<K, V> by keyValuePairs {
    var keyType: Type? = null
    var valType: Type? = null
    var defaultValue: Any? = null

    @Suppress("UNCHECKED_CAST")
    override fun get(key: K): V? {
        val value = keyValuePairs[key]
        return value ?: return defaultValue as V
    }

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
        if (defaultValue is String) {
            data.writeOpcode(3)
            data.writeStringCP1252(defaultValue as String)
        } else {
            data.writeOpcode(4)
            data.writeInt(encodeEnumType(defaultValue as Any))
        }
        when {
            keyValuePairs.all { it.value is String } -> {
                data.writeOpcode(5)
                keyValuePairs.forEach { (key, value) ->
                    data.writeInt(encodeEnumType(key as Any))
                    data.writeStringCP1252(value as String)
                }
            }
            keyValuePairs.all { it.value is Int } -> {
                data.writeOpcode(6)
                keyValuePairs.forEach { (key, value) ->
                    data.writeInt(encodeEnumType(key as Any))
                    data.writeInt(encodeEnumType(value as Any))
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

    public enum class Type(public val letter: Char) {
        BOOLEAN('1'), SEQUENCE('A'), INTEGER('i'), COMPONENT('I'), OBJ('o'), NAMED_OBJ('O'), STRING('s'), STAT('S'),
        INV('v'), NPC('n'), MIDI('m'), GRAPHIC('d'), ENUM('g'), LOC('l'), STRUCT('J'), MAP_AREA('`'), CATEGORY('y'),
        AREA('R'), COORDINATE('c'), COLOUR('C'), MODEL('m'), IDKIT('K'), FONT_METRICS('f'), CHAT_CHAR('k'), CHAR('z');

        public companion object {
            public operator fun invoke(char: Char?): Type? = values().find { it.letter == char }
        }
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

    public companion object : ConfigCompanion<EnumConfig<Any, Any>>() {
        override val id: Int = 8

        public fun decodeEnumType(type: Type?, value: Int): Any = when (type) {
            Type.BOOLEAN -> value == 1
            Type.COMPONENT -> Component.decode(value)
            Type.STAT -> Stat.decode(value)
            Type.COORDINATE -> Coord.decode(value)
            Type.INTEGER, Type.OBJ, Type.NAMED_OBJ, Type.INV, Type.GRAPHIC, Type.ENUM,
            Type.LOC, Type.STRUCT, Type.MAP_AREA, Type.CATEGORY, Type.AREA, Type.COLOUR,
            Type.MODEL -> value
            else -> throw IOException("Could not decode type $type as int.")
        }

        override fun decode(id: Int, data: ByteBuf): EnumConfig<Any, Any> {
            val enumConfig = EnumConfig<Any, Any>(id)
            decoder@ while (true) {
                when (val opcode = data.readUnsignedByte().toInt()) {
                    0 -> break@decoder
                    1 -> enumConfig.keyType = Type(data.readUnsignedByte().toChar())
                    2 -> enumConfig.valType = Type(data.readUnsignedByte().toChar())
                    3 -> enumConfig.defaultValue = data.readStringCP1252()
                    4 -> enumConfig.defaultValue = decodeEnumType(enumConfig.valType, data.readInt())
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
