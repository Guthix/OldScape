/**
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
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.cache.binary

import io.netty.buffer.ByteBuf

public class Huffman(private val frequencies: ByteArray, private val masks: IntArray, private val keys: IntArray) {
    public fun compress(text: String): ByteArray {
        val output = ByteArray(256)
        var key = 0

        var currentPos = 0
        for (element in text) {
            val curChar = element.toInt() and 255
            val frequency = frequencies[curChar]
            val encoding = masks[curChar]
            if (frequency.toInt() == 0) throw RuntimeException("No codeword for data value $curChar.")
            var remainder = currentPos and 7
            key = key and (-remainder shr 31)
            var outIndex = currentPos shr 3
            currentPos += frequency.toInt()
            val i_41_ = (-1 + (remainder - -frequency) shr 3) + outIndex
            remainder += 24
            key = key or encoding.ushr(remainder)
            output[outIndex] = key.toByte()
            if (i_41_.inv() < outIndex.inv()) {
                remainder -= 8
                key = encoding.ushr(remainder)
                output[++outIndex] = key.toByte()
                if (outIndex.inv() > i_41_.inv()) {
                    remainder -= 8
                    key = encoding.ushr(remainder)
                    output[++outIndex] = key.toByte()
                    if (outIndex.inv() > i_41_.inv()) {
                        remainder -= 8
                        key = encoding.ushr(remainder)
                        output[++outIndex] = key.toByte()
                        if (i_41_ > outIndex) {
                            remainder -= 8
                            key = encoding shl -remainder
                            output[++outIndex] = key.toByte()
                        }
                    }
                }
            }
        }
        return output.sliceArray(0..(7 + currentPos shr 3))
    }

    public fun decompress(compressed: ByteArray, decompressedLength: Int): ByteArray {
        val decompressed = ByteArray(decompressedLength)
        if (decompressedLength == 0) {
            return decompressed
        } else {
            var decompIndex = 0
            var keyIndex = 0
            var compIndex = 0
            while (true) {
                val curChar = compressed[compIndex]

                if (curChar < 0) keyIndex = keys[keyIndex] else ++keyIndex
                var resultByte: Int = keys[keyIndex]
                if (resultByte < 0) {
                    decompressed[decompIndex++] = resultByte.inv().toByte()
                    if (decompIndex >= decompressedLength) break
                    keyIndex = 0
                }

                if (curChar.toInt() and 0x40 != 0) keyIndex = keys[keyIndex] else ++keyIndex
                resultByte = keys[keyIndex]
                if (resultByte < 0) {
                    decompressed[decompIndex++] = resultByte.inv().toByte()
                    if (decompIndex >= decompressedLength) break
                    keyIndex = 0
                }

                if (curChar.toInt() and 0x20 != 0) keyIndex = keys[keyIndex] else ++keyIndex
                resultByte = keys[keyIndex]
                if (resultByte < 0) {
                    decompressed[decompIndex++] = resultByte.inv().toByte()
                    if (decompIndex >= decompressedLength) break
                    keyIndex = 0
                }

                if (curChar.toInt() and 0x10 != 0) keyIndex = keys[keyIndex] else ++keyIndex
                resultByte = keys[keyIndex]
                if (resultByte < 0) {
                    decompressed[decompIndex++] = resultByte.inv().toByte()
                    if (decompIndex >= decompressedLength) break
                    keyIndex = 0
                }

                if (curChar.toInt() and 0x8 != 0) keyIndex = keys[keyIndex] else ++keyIndex
                resultByte = keys[keyIndex]
                if (resultByte < 0) {
                    decompressed[decompIndex++] = resultByte.inv().toByte()
                    if (decompIndex >= decompressedLength) break
                    keyIndex = 0
                }

                if (curChar.toInt() and 0x4 != 0) keyIndex = keys[keyIndex] else ++keyIndex
                resultByte = keys[keyIndex]
                if (resultByte < 0) {
                    decompressed[decompIndex++] = resultByte.inv().toByte()
                    if (decompIndex >= decompressedLength) break
                    keyIndex = 0
                }

                if (curChar.toInt() and 0x2 != 0) keyIndex = keys[keyIndex] else ++keyIndex
                resultByte = keys[keyIndex]
                if (resultByte < 0) {
                    decompressed[decompIndex++] = resultByte.inv().toByte()
                    if (decompIndex >= decompressedLength) break
                    keyIndex = 0
                }

                if (curChar.toInt() and 0x1 != 0) keyIndex = keys[keyIndex] else ++keyIndex
                resultByte = keys[keyIndex]
                if (resultByte < 0) {
                    decompressed[decompIndex++] = resultByte.inv().toByte()
                    if (decompIndex >= decompressedLength) break
                    keyIndex = 0
                }

                ++compIndex
            }
            return decompressed
        }
    }

    public companion object {
        public fun load(buf: ByteBuf): Huffman {
            val frequencies = ByteArray(buf.readableBytes()).apply {
                buf.readBytes(this)
            }
            val masks = IntArray(frequencies.size)
            val frequencyMasks = IntArray(33)
            var keys = IntArray(8)
            var biggestId = 0
            frequencies.forEachIndexed { i, freq ->
                if (freq.toInt() != 0) {
                    val frequencyMask = 1 shl (32 - freq)
                    val mask = frequencyMasks[freq.toInt()]
                    masks[i] = mask
                    val i_9: Int
                    var keyId: Int
                    var i_11: Int
                    var someOtherMask: Int
                    if (mask and frequencyMask != 0) {
                        i_9 = frequencyMasks[freq - 1]
                    } else {
                        i_9 = mask or frequencyMask
                        keyId = freq - 1
                        while (keyId >= 1) {
                            i_11 = frequencyMasks[keyId]
                            if (i_11 != mask) break
                            someOtherMask = 1 shl (32 - keyId)
                            if (i_11 and someOtherMask != 0) {
                                frequencyMasks[keyId] = frequencyMasks[keyId - 1]
                                break
                            }
                            frequencyMasks[keyId] = i_11 or someOtherMask
                            --keyId
                        }
                    }

                    frequencyMasks[freq.toInt()] = i_9

                    keyId = freq + 1
                    while (keyId <= 32) {
                        if (frequencyMasks[keyId] == mask)
                            frequencyMasks[keyId] = i_9
                        keyId++
                    }

                    keyId = 0

                    i_11 = 0
                    while (i_11 < freq) {
                        someOtherMask = Integer.MIN_VALUE.ushr(i_11)
                        if (mask and someOtherMask != 0) {
                            if (keys[keyId] == 0)
                                keys[keyId] = biggestId

                            keyId = keys[keyId]
                        } else {
                            ++keyId
                        }
                        if (keyId >= keys.size) {
                            val ints_13 = IntArray(keys.size * 2)
                            for (i_14 in keys.indices) {
                                ints_13[i_14] = keys[i_14]
                            }
                            keys = ints_13
                        }
                        i_11++
                    }

                    keys[keyId] = i.inv()
                    if (keyId >= biggestId) biggestId = keyId + 1
                }
            }
            return Huffman(frequencies, masks, keys)
        }
    }
}
