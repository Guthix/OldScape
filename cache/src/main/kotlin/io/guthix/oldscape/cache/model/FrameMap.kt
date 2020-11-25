/*
 * Copyright 2018-2020 Guthix
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

import io.netty.buffer.ByteBuf

public class FrameMap(public val types: ShortArray, public val frameMaps: Array<ShortArray>) {
    public companion object {
        public fun decode(data: ByteBuf): FrameMap {
            val length = data.readUnsignedByte().toInt()
            val types = ShortArray(length) {
                data.readUnsignedByte()
            }
            val frameMaps = Array(length) {
                ShortArray(data.readUnsignedByte().toInt())
            }
            frameMaps[0].map { data.readUnsignedByte() }

            frameMaps.forEach { frameMap ->
                for(i in frameMap.indices) {
                    frameMap[i] = data.readUnsignedByte()
                }
            }
            return FrameMap(types, frameMaps)
        }
    }
}