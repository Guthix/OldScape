/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.net.state

class IsaacRandomPair(val encodeGen : IsaacRandom, val decodeGen: IsaacRandom)

class IsaacRandom(private val seed: IntArray) {
    private var valuesRemaining = 0
    private var randResult: IntArray = IntArray(256)
    private var mm: IntArray = IntArray(256)
    private var field2387 = 0
    private var field2385 = 0
    private var field2382 = 0

    init {
        for (int_0 in seed.indices) {
            randResult[int_0] = seed[int_0]
        }
        method3861()
    }

    fun nextInt(): Int {
        if (--valuesRemaining + 1 == 0) {
            generateMoreResults()
            valuesRemaining = 255
        }
        return randResult[valuesRemaining]
    }

    fun generateMoreResults() {
        field2385 += ++field2382

        for (int_0 in 0..255) {
            val int_1 = mm[int_0]
            field2387 = if (int_0 and 0x2 == 0) {
                if (int_0 and 0x1 == 0) {
                    field2387 xor (field2387 shl 13)
                } else {
                    field2387 xor field2387.ushr(6)
                }
            } else if (int_0 and 0x1 == 0) {
                field2387 xor (field2387 shl 2)
            } else {
                field2387 xor field2387.ushr(16)
            }

            field2387 += mm[128 + int_0 and 0xFF]
            val int_2: Int
            int_2 = mm[int_1 and 0x3FC shr 2] + field2385 + field2387
            mm[int_0] = int_2
            field2385 = mm[int_2 shr 8 and 0x3FC shr 2] + int_1
            randResult[int_0] = field2385
        }

    }

    private fun method3861() {
        var int_0 = GOLDEN_RATIO
        var int_1 = GOLDEN_RATIO
        var int_2 = GOLDEN_RATIO
        var int_3 = GOLDEN_RATIO
        var int_4 = GOLDEN_RATIO
        var int_5 = GOLDEN_RATIO
        var int_6 = GOLDEN_RATIO
        var int_7 = GOLDEN_RATIO

        var int_8 = 0
        while (int_8 < 4) {
            int_7 = int_7 xor (int_6 shl 11)
            int_4 += int_7
            int_6 += int_5
            int_6 = int_6 xor int_5.ushr(2)
            int_3 += int_6
            int_5 += int_4
            int_5 = int_5 xor (int_4 shl 8)
            int_2 += int_5
            int_4 += int_3
            int_4 = int_4 xor int_3.ushr(16)
            int_1 += int_4
            int_3 += int_2
            int_3 = int_3 xor (int_2 shl 10)
            int_0 += int_3
            int_2 += int_1
            int_2 = int_2 xor int_1.ushr(4)
            int_7 += int_2
            int_1 += int_0
            int_1 = int_1 xor (int_0 shl 8)
            int_6 += int_1
            int_0 += int_7
            int_0 = int_0 xor int_7.ushr(9)
            int_5 += int_0
            int_7 += int_6
            int_8++
        }

        int_8 = 0
        while (int_8 < 256) {
            int_7 += randResult[int_8]
            int_6 += randResult[int_8 + 1]
            int_5 += randResult[int_8 + 2]
            int_4 += randResult[int_8 + 3]
            int_3 += randResult[int_8 + 4]
            int_2 += randResult[int_8 + 5]
            int_1 += randResult[int_8 + 6]
            int_0 += randResult[int_8 + 7]
            int_7 = int_7 xor (int_6 shl 11)
            int_4 += int_7
            int_6 += int_5
            int_6 = int_6 xor int_5.ushr(2)
            int_3 += int_6
            int_5 += int_4
            int_5 = int_5 xor (int_4 shl 8)
            int_2 += int_5
            int_4 += int_3
            int_4 = int_4 xor int_3.ushr(16)
            int_1 += int_4
            int_3 += int_2
            int_3 = int_3 xor (int_2 shl 10)
            int_0 += int_3
            int_2 += int_1
            int_2 = int_2 xor int_1.ushr(4)
            int_7 += int_2
            int_1 += int_0
            int_1 = int_1 xor (int_0 shl 8)
            int_6 += int_1
            int_0 += int_7
            int_0 = int_0 xor int_7.ushr(9)
            int_5 += int_0
            int_7 += int_6
            mm[int_8] = int_7
            mm[int_8 + 1] = int_6
            mm[int_8 + 2] = int_5
            mm[int_8 + 3] = int_4
            mm[int_8 + 4] = int_3
            mm[int_8 + 5] = int_2
            mm[int_8 + 6] = int_1
            mm[int_8 + 7] = int_0
            int_8 += 8
        }

        int_8 = 0
        while (int_8 < 256) {
            int_7 += mm[int_8]
            int_6 += mm[int_8 + 1]
            int_5 += mm[int_8 + 2]
            int_4 += mm[int_8 + 3]
            int_3 += mm[int_8 + 4]
            int_2 += mm[int_8 + 5]
            int_1 += mm[int_8 + 6]
            int_0 += mm[int_8 + 7]
            int_7 = int_7 xor (int_6 shl 11)
            int_4 += int_7
            int_6 += int_5
            int_6 = int_6 xor int_5.ushr(2)
            int_3 += int_6
            int_5 += int_4
            int_5 = int_5 xor (int_4 shl 8)
            int_2 += int_5
            int_4 += int_3
            int_4 = int_4 xor int_3.ushr(16)
            int_1 += int_4
            int_3 += int_2
            int_3 = int_3 xor (int_2 shl 10)
            int_0 += int_3
            int_2 += int_1
            int_2 = int_2 xor int_1.ushr(4)
            int_7 += int_2
            int_1 += int_0
            int_1 = int_1 xor (int_0 shl 8)
            int_6 += int_1
            int_0 += int_7
            int_0 = int_0 xor int_7.ushr(9)
            int_5 += int_0
            int_7 += int_6
            mm[int_8] = int_7
            mm[int_8 + 1] = int_6
            mm[int_8 + 2] = int_5
            mm[int_8 + 3] = int_4
            mm[int_8 + 4] = int_3
            mm[int_8 + 5] = int_2
            mm[int_8 + 6] = int_1
            mm[int_8 + 7] = int_0
            int_8 += 8
        }

        generateMoreResults()
        valuesRemaining = 256
    }

    companion object {
        const val GOLDEN_RATIO = -1640531527
    }
}
