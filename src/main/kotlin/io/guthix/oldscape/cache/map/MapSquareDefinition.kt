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

package io.guthix.oldscape.cache.map

import io.guthix.buffer.readIncrSmallSmart
import io.guthix.buffer.readUnsignedSmallSmart
import io.netty.buffer.ByteBuf
import kotlin.math.cos

class MapSquareDefinition(
    val x: Int,
    val y: Int,
    val mapDefinition: MapDefinition,
    val locationDefinitions: List<MapLocDefinition>
) {
    companion object {
        const val FLOOR_COUNT = 4

        const val SIZE = 64

        fun decode(landData: ByteBuf, mapData: ByteBuf, x: Int, y: Int): MapSquareDefinition {
            val mapDefinitions = MapDefinition.decode(landData, x, y)
            val mapLocDefinitions = MapLocDefinition.decode(mapData, mapDefinitions.renderRules)
            return MapSquareDefinition(x, y, mapDefinitions, mapLocDefinitions)
        }
    }
}

class MapDefinition(
    val tileHeights: Array<Array<IntArray>>,
    val renderRules: Array<Array<ShortArray>>,
    val overlayIds: Array<Array<ByteArray>>,
    val overlayPaths: Array<Array<ShortArray>>,
    val overlayRotations: Array<Array<ShortArray>>,
    val underlayIds: Array<Array<ShortArray>>
) {
    companion object {
        const val BLOCKED_TILE_MASK: Short = 0x1
        const val BRIDGE_TILE_MASK: Short = 0x2
        const val ROOF_TILE_MASK: Short = 0x4
        private const val JAGEX_CIRCULAR_ANGLE = 2048
        private const val ANGULAR_RATIO = 360.0 / JAGEX_CIRCULAR_ANGLE
        private val JAGEX_RADIAN = Math.toRadians(ANGULAR_RATIO)
        private val COS = IntArray(JAGEX_CIRCULAR_ANGLE) {
            ((0xFFFF + 1) * cos(it.toDouble() * JAGEX_RADIAN).toInt())
        }

        fun decode(data: ByteBuf, baseX: Int, baseY: Int): MapDefinition {
            val tileHeights = Array(MapSquareDefinition.FLOOR_COUNT) { Array(MapSquareDefinition.SIZE) { IntArray(
                MapSquareDefinition.SIZE
            ) } }
            val renderRules = Array(MapSquareDefinition.FLOOR_COUNT) { Array(MapSquareDefinition.SIZE) { ShortArray(
                MapSquareDefinition.SIZE
            ) } }
            val overlayIds = Array(MapSquareDefinition.FLOOR_COUNT) { Array(MapSquareDefinition.SIZE) { ByteArray(
                MapSquareDefinition.SIZE
            ) } }
            val overlayPaths = Array(MapSquareDefinition.FLOOR_COUNT) { Array(MapSquareDefinition.SIZE) { ShortArray(
                MapSquareDefinition.SIZE
            ) } }
            val overlayRotations = Array(MapSquareDefinition.FLOOR_COUNT) { Array(MapSquareDefinition.SIZE) { ShortArray(
                MapSquareDefinition.SIZE
            ) } }
            val underlayIds = Array(MapSquareDefinition.FLOOR_COUNT) { Array(MapSquareDefinition.SIZE) { ShortArray(
                MapSquareDefinition.SIZE
            ) } }
            for (z in 0 until MapSquareDefinition.FLOOR_COUNT) {
                for (x in 0 until MapSquareDefinition.SIZE) {
                    for (y in 0 until MapSquareDefinition.SIZE) {
                        loop@ while (true) {
                            val opcode = data.readUnsignedByte().toInt()
                            when {
                                opcode == 0 -> {
                                    if(z == 0) {
                                        tileHeights[0][x][y] = calcZ0Height(baseX, baseY, x, y)
                                    } else {
                                        tileHeights[z][x][y] = tileHeights[z - 1][x][y] - 240
                                    }
                                    break@loop
                                }
                                opcode == 1 -> {
                                    var height = data.readUnsignedByte().toInt()
                                    if (height == 1) height = 0
                                    if(z == 0) {
                                        tileHeights[0][x][y] = -height * 8
                                    } else {
                                        tileHeights[z][x][y] = tileHeights[z - 1][x][y] - height * 8
                                    }
                                    break@loop
                                }
                                opcode <= 49 -> {
                                    overlayIds[z][x][y] = data.readByte()
                                    overlayPaths[z][x][y] = ((opcode - 2) shr 2).toShort()
                                    overlayRotations[z][x][y] = ((opcode - 2) and 0x3).toShort()
                                }
                                opcode <= 89 -> renderRules[z][x][y] = (opcode - 49).toShort()
                                else -> underlayIds[z][x][y] = (opcode - 81).toShort()
                            }
                        }
                    }
                }
            }
            return MapDefinition(tileHeights, renderRules, overlayIds, overlayPaths, overlayRotations, underlayIds)
        }

        private fun calcZ0Height(baseX: Int, baseY: Int, x: Int, y: Int): Int {
            val xc = x + baseX + 932731
            val yc = y + baseY + 556238
            var height = interpolateNoise(45365 + xc, yc + 91923, 4) - 128
                    + (interpolateNoise(10294 + xc, 37821 + yc, 2) - 128 shr 1)
                    + (interpolateNoise(xc, yc, 1) - 128 shr 2)
            height = (height * 0.3).toInt() + 35
            if (height < 10)  height = 10 else if (height > 60) height = 60
            return -height * 8
        }

        private fun interpolateNoise(x: Int, y: Int, frequency: Int): Int {
            val intX = x / frequency
            val fracX = x and (frequency - 1)
            val intY = y / frequency
            val fracY = y and (frequency - 1)
            val v1 = smoothNoise2d(intX, intY)
            val v2 = smoothNoise2d(intX + 1, intY)
            val v3 = smoothNoise2d(intX, intY + 1)
            val v4 = smoothNoise2d(1 + intX, 1 + intY)
            val i1 = interpolate(v1, v2, fracX, frequency)
            val i2 = interpolate(v3, v4, fracX, frequency)
            return interpolate(i1, i2, fracY, frequency)
        }

        private fun smoothNoise2d(x: Int, y: Int): Int {
            val corners = noise(x - 1, y - 1) + noise(x + 1, y - 1) + noise(x - 1, 1 + y)
            + noise(x + 1, y + 1)
            val sides = noise(x - 1, y) + noise(1 + x, y) + noise(x, y - 1) + noise(x, 1 + y)
            val center = noise(x, y)
            return corners / 16 + sides / 8 + center / 4
        }

        private fun noise(x: Int, y: Int): Int {
            var n = x + y * 57
            n = n xor (n shl 13)
            return (n * n * 15731 + 789221) * n + 1376312589 and (Int.MAX_VALUE shr 19) and 0xFF
        }

        private fun interpolate(a: Int, b: Int, x: Int, y: Int): Int {
            val f = (0xFFFF + 1) - COS[(JAGEX_CIRCULAR_ANGLE / 2) * x / y] shr 1
            return (f * b shr 16) + (a * ((0xFFFF + 1) - f) shr 16)
        }
    }
}

class MapLocDefinition(
    val id: Int,
    val floor: Int,
    val localX: Int,
    val localY: Int,
    val type: Int,
    val orientation: Int
) {
    companion object {
        fun decode(data: ByteBuf, renderRules: Array<Array<ShortArray>>): List<MapLocDefinition> {
            var id = -1
            var offset = data.readIncrSmallSmart()
            val locations = mutableListOf<MapLocDefinition>()
            while (offset != 0) {
                id += offset
                var positionHash = 0
                var positionOffset = data.readUnsignedSmallSmart()
                while (positionOffset != 0) {
                    positionHash += positionOffset - 1
                    val localY = positionHash and 0x3F
                    val localX = (positionHash shr 6) and 0x3F
                    var z = (positionHash shr 12) and 0x3
                    if(renderRules[1][localX][localY] == MapDefinition.BRIDGE_TILE_MASK) z--
                    if(z < 0) {
                        data.readByte()
                    } else {
                        val attributes = data.readUnsignedByte().toInt()
                        val orientation = attributes and 0x3
                        val type = attributes shr 2
                        locations.add(MapLocDefinition(id, z, localX, localY, type, orientation))
                    }
                    positionOffset = data.readUnsignedSmallSmart()
                }
                offset = data.readIncrSmallSmart()
            }
            return locations
        }
    }
}