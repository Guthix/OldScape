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
@file:Suppress("unused")
package io.guthix.oldscape.cache.plane

import io.netty.buffer.ByteBuf
import java.io.IOException

class Texture(
    val id: Int,
    val field1527: Int,
    val field1530: Boolean,
    val fileIds: IntArray,
    val field1535: ShortArray?,
    val field1532: ShortArray?,
    val field1536: IntArray,
    val field1537: Short,
    val field1538: Short
) {
    companion object {
        fun decode(id: Int, data: ByteBuf): Texture {
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