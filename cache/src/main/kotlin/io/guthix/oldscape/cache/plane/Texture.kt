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
package io.guthix.oldscape.cache.plane

import io.netty.buffer.ByteBuf
import java.io.IOException

public class Texture(
    public val id: Int,
    public val field1527: Int,
    public val field1530: Boolean,
    public val fileIds: IntArray,
    public val field1535: ShortArray?,
    public val field1532: ShortArray?,
    public val field1536: IntArray,
    public val field1537: Short,
    public val field1538: Short
) {
    public companion object {
        public fun decode(id: Int, data: ByteBuf): Texture {
            val field1527 = data.readUnsignedShort()
            val field1530 = data.readUnsignedByte().toInt() == 1
            val amount = data.readUnsignedByte().toInt()
            if(amount !in 0..4) throw IOException("Amount of textures should be between 0 and 4 but is $amount.")
            val fileIds = IntArray(amount) {
                data.readUnsignedShort()
            }
            val (field1535, field1532) = if (amount > 1) {
                Pair(
                    ShortArray(amount - 1) {
                        data.readUnsignedByte()
                    },
                    ShortArray(amount - 1) {
                        data.readUnsignedByte()
                    }
                )
            } else Pair(null, null)
            val field1536 = IntArray(amount) {
                data.readInt()
            }
            val field1537 = data.readUnsignedByte()
            val field1538 = data.readUnsignedByte()
            return Texture(id, field1527, field1530, fileIds, field1535, field1532, field1536, field1537, field1538)
        }
    }
}