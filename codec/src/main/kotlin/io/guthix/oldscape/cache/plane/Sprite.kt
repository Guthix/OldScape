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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.cache.plane

import io.netty.buffer.ByteBuf
import java.awt.image.BufferedImage

class Sprite(val id: Int, val width: Int, val height: Int, val images: Array<BufferedImage>) {
    companion object {
        private const val FLAG_VERTICAL = 0x01
        private const val FLAG_ALPHA = 0x02

        fun decode(id: Int, data: ByteBuf): Sprite {
            data.readerIndex(data.writerIndex() - 2)
            val spriteCount = data.readUnsignedShort()
            val offsetsX = IntArray(spriteCount)
            val offsetsY = IntArray(spriteCount)
            val subWidths = IntArray(spriteCount)
            val subHeights = IntArray(spriteCount)
            data.readerIndex(data.writerIndex() - spriteCount * 8 - 7)
            val width = data.readUnsignedShort()
            val height = data.readUnsignedShort()
            val palette = IntArray(data.readUnsignedByte().toInt() + 1)
            for(i in 0 until spriteCount) {
                offsetsX[i] = data.readUnsignedShort()
            }
            for(i in 0 until spriteCount) {
                offsetsY[i] = data.readUnsignedShort()
            }
            for(i in 0 until spriteCount) {
                subWidths[i] = data.readUnsignedShort()
            }
            for(i in 0 until spriteCount) {
                subHeights[i] = data.readUnsignedShort()
            }

            // read palette
            data.readerIndex(data.writerIndex() - 7 - spriteCount * 8 - (palette.size - 1) * 3)
            for (i in 1 until palette.size) {
                palette[i] = data.readUnsignedMedium()
                if (palette[i] == 0) palette[i] = 1
            }

            // read pixels
            data.readerIndex(0)
            val images = Array(spriteCount) {
                val subWidth = subWidths[it]
                val subHeight = subHeights[it]
                val offsetX = offsetsX[it]
                val offsetY = offsetsY[it]
                val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
                val indices = Array(subWidth) { IntArray(subHeight) }
                val flags = data.readUnsignedByte().toInt()
                if (flags and FLAG_VERTICAL != 0) { // read rgb vertical first
                    for (x in 0 until subWidth) {
                        for (y in 0 until subHeight) {
                            indices[x][y] = data.readUnsignedByte().toInt()
                        }
                    }
                } else { // read rgb horizontal first
                    for (y in 0 until subHeight) {
                        for (x in 0 until subWidth) {
                            indices[x][y] = data.readUnsignedByte().toInt()
                        }
                    }
                }
                if (flags and FLAG_ALPHA != 0) { // set rgb with alpha
                    if (flags and FLAG_VERTICAL != 0) { // read alpha vertical first
                        for (x in 0 until subWidth) {
                            for (y in 0 until subHeight) {
                                val alpha = data.readUnsignedByte().toInt()
                                image.setRGB(x + offsetX, y + offsetY, alpha shl 24 or palette[indices[x][y]])
                            }
                        }
                    } else { // read alpha horizontal first
                        for (y in 0 until subHeight) {
                            for (x in 0 until subWidth) {
                                val alpha = data.readUnsignedByte().toInt()
                                image.setRGB(x + offsetX, y + offsetY, alpha shl 24 or palette[indices[x][y]])
                            }
                        }
                    }
                } else { // set rgb without alpha
                    for (x in 0 until subWidth) {
                        for (y in 0 until subHeight) {
                            val index = indices[x][y]
                            if (index == 0) {
                                image.setRGB(x + offsetX, y + offsetY, 0)
                            } else {
                                image.setRGB(x + offsetX, y + offsetY, -0x1000000 or palette[index])
                            }
                        }
                    }
                }
                image
            }
            return Sprite(id, width, height, images)
        }
    }
}