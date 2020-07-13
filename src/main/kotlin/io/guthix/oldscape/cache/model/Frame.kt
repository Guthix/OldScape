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

package io.guthix.oldscape.cache.model

import io.guthix.buffer.readSmallSmart
import io.netty.buffer.ByteBuf

public class Frame(
    public val frameMapId: Int,
    public val translateX: IntArray,
    public val translateY: IntArray,
    public val translateZ: IntArray,
    public val translateCount: Int?,
    public val indexFrameIds: IntArray,
    public val showing: Boolean
) {
    public companion object {
        public fun decode(frameMaps: FrameMap, data: ByteBuf): Frame {
            val data0 = data.duplicate()
            val frameMapId = data.readUnsignedShort()
            val length = data.readUnsignedByte().toInt()
            data0.skipBytes(3 + length)
            val indexFrameIds = IntArray(500)
            val translatorX = IntArray(500)
            val translatorY = IntArray(500)
            val translatorZ = IntArray(500)

            var lastI = -1
            var index = 0
            var showing = false
            for (i in 0 until length) {
                val opcode = data.readUnsignedByte().toInt()
                if (opcode <= 0) {
                    continue
                }
                indexFrameIds[index] = i
                if (frameMaps.types[i].toInt() != 0) {
                    for(j in i - 1 downTo lastI + 1) {
                        if (frameMaps.types[j].toInt() == 0) {
                            indexFrameIds[index] = j
                            translatorX[index] = 0
                            translatorY[index] = 0
                            translatorZ[index] = 0
                            index++
                            break
                        }

                    }
                }
                var var11 = 0
                if (frameMaps.types[i].toInt() == 3) {
                    var11 = 128
                }

                if (opcode and 1 != 0) {
                    translatorX[index] = data0.readSmallSmart()
                } else {
                    translatorX[index] = var11
                }

                if (opcode and 2 != 0) {
                    translatorY[index] = data0.readSmallSmart()
                } else {
                    translatorY[index] = var11
                }

                if (opcode and 4 != 0) {
                    translatorZ[index] = data0.readSmallSmart()
                } else {
                    translatorZ[index] = var11
                }
                if (frameMaps.types[i].toInt() == 5) {
                    showing = true
                }
                lastI = i
                index++
            }
            return Frame(frameMapId, translatorX, translatorY, translatorZ, length, indexFrameIds, showing)
        }
    }
}