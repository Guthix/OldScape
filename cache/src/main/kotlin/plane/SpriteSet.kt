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
import io.netty.buffer.Unpooled
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.util.*

public data class Sprite(val offsetX: Int, val offsetY: Int, val image: BufferedImage) {
    public val width: Int get() = image.width
    public val height: Int get() = image.height
    public fun getRGB(x: Int, y: Int): Int = image.getRGB(x, y)
    public fun setRGB(x: Int, y: Int, rgb: Int) {
        image.setRGB(x, y, rgb)
    }
}

public data class SpriteSet(
    public val id: Int,
    public val width: Int,
    public val height: Int,
    public val sprites: List<Sprite>
) {
    public fun encode(): ByteBuf {
        val bout = ByteArrayOutputStream()
        val dout = DataOutputStream(bout)
        return dout.use { os ->
            val palette: MutableList<Int> = ArrayList()
            palette.add(0)

            for (sprite in sprites) {
                var flags = FLAG_VERTICAL
                for (x in 0 until sprite.width) {
                    for (y in 0 until sprite.height) {
                        val argb = sprite.getRGB(x, y)
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
                for (x in 0 until sprite.width) {
                    for (y in 0 until sprite.height) {
                        val argb = sprite.getRGB(x, y)
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
                            .map { sprite.getRGB(x, it) }
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

            for ((offsetX) in sprites) os.writeShort(offsetX) // set x offset to 0
            for (sprite in sprites) os.writeShort(sprite.offsetY)
            for (sprite in sprites) os.writeShort(sprite.width)
            for (sprite in sprites) os.writeShort(sprite.height)
            os.writeShort(sprites.size)
            Unpooled.wrappedBuffer(bout.toByteArray())
        }
    }

    public companion object {
        private const val FLAG_VERTICAL = 0x01
        private const val FLAG_ALPHA = 0x02

        public fun decode(id: Int, data: ByteBuf): SpriteSet {
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
            for (i in 0 until spriteCount) offsetsX[i] = data.readUnsignedShort()
            for (i in 0 until spriteCount) offsetsY[i] = data.readUnsignedShort()
            for (i in 0 until spriteCount) subWidths[i] = data.readUnsignedShort()
            for (i in 0 until spriteCount) subHeights[i] = data.readUnsignedShort()

            // read palette
            data.readerIndex(data.writerIndex() - 7 - spriteCount * 8 - (palette.size - 1) * 3)
            for (i in 1 until palette.size) {
                palette[i] = data.readUnsignedMedium()
                if (palette[i] == 0) palette[i] = 1
            }

            // read pixels
            data.readerIndex(0)
            val images = mutableListOf<Sprite>()
            for (i in 0 until spriteCount) {
                val subWidth = subWidths[i]
                val subHeight = subHeights[i]
                val offsetX = offsetsX[i]
                val offsetY = offsetsY[i]
                if (subWidth == 0 || subHeight == 0) {
                    data.skipBytes(1 + subHeight * subWidth * 2)
                    continue
                }
                val image = Sprite(offsetX, offsetY, BufferedImage(subWidth, subHeight, BufferedImage.TYPE_INT_ARGB))
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
                                val color = palette[indices[x][y]]
                                image.setRGB(x, y, alpha shl 24 or color)
                            }
                        }
                    } else { // read alpha horizontal first
                        for (y in 0 until subHeight) {
                            for (x in 0 until subWidth) {
                                val alpha = data.readUnsignedByte().toInt()
                                val color = palette[indices[x][y]]
                                image.setRGB(x, y, alpha shl 24 or color)
                            }
                        }
                    }
                } else { // set rgb without alpha
                    for (x in 0 until subWidth) {
                        for (y in 0 until subHeight) {
                            val index = indices[x][y]
                            if (index == 0) {
                                image.setRGB(x, y, 0)
                            } else {
                                image.setRGB(x, y, -0x1000000 or palette[index])
                            }
                        }
                    }
                }
                images.add(image)
            }
            return SpriteSet(id, width, height, images)
        }
    }
}