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
package io.guthix.oldscape.cache.plane

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.util.*

public class Sprite(
    public val id: Int,
    public val width: Int,
    public val height: Int,
    public val images: Array<BufferedImage>
) {
    public fun encode(): ByteBuf {
        check(images.all { it.width == width && it.height == height } ) {
            "All images must have the same height and width."
        }
        val bout = ByteArrayOutputStream()
        val dout = DataOutputStream(bout)
        return dout.use { os ->
            val palette: MutableList<Int> = ArrayList()
            palette.add(0)

            for (image in images) {
                var flags = FLAG_VERTICAL
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        val argb = image.getRGB(x, y)
                        val alpha = argb shr 24 and 0xFF
                        var rgb = argb and 0xFFFFFF
                        if (rgb == 0) rgb = 1
                        if (alpha != 0 && alpha != 255) flags = flags or FLAG_ALPHA
                        if (!palette.contains(rgb)) {
                            if (palette.size >= 256) throw IOException("Palette size to big.")
                            palette.add(rgb)
                        }
                    }
                }

                os.write(flags)
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        val argb = image.getRGB(x, y)
                        val alpha = argb shr 24 and 0xFF
                        var rgb = argb and 0xFFFFFF
                        if (rgb == 0) rgb = 1
                        if (flags and FLAG_ALPHA == 0 && alpha == 0) {
                            os.write(0)
                        } else {
                            os.write(palette.indexOf(rgb))
                        }
                    }
                }

                if (flags and FLAG_ALPHA != 0) {
                    for (x in 0 until width) {
                        (0 until height)
                            .asSequence()
                            .map { image.getRGB(x, it) }
                            .map { it shr 24 and 0xFF }
                            .forEach(os::write)
                    }
                }
            }

            for (i in 1 until palette.size) {
                val rgb = palette[i]
                os.write(rgb shr 16)
                os.write(rgb shr 8)
                os.write(rgb)
            }

            os.writeShort(width)
            os.writeShort(height)
            os.write(palette.size - 1)

            for (i in images.indices) {
                os.writeShort(0) // set x offset to 0
                os.writeShort(0) // set y offset to 0
                os.writeShort(width)
                os.writeShort(height)
            }
            os.writeShort(images.size)
            Unpooled.wrappedBuffer(bout.toByteArray())
        }
    }

    public companion object {
        private const val FLAG_VERTICAL = 0x01
        private const val FLAG_ALPHA = 0x02

        public fun decode(id: Int, data: ByteBuf): Sprite {
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
            for (i in 0 until spriteCount) {
                offsetsX[i] = data.readUnsignedShort()
            }
            for (i in 0 until spriteCount) {
                offsetsY[i] = data.readUnsignedShort()
            }
            for (i in 0 until spriteCount) {
                subWidths[i] = data.readUnsignedShort()
            }
            for (i in 0 until spriteCount) {
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