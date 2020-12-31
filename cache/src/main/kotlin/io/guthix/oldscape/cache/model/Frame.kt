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