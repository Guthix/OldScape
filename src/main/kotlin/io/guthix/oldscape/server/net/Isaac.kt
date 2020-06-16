/**
 * This file is part of Guthix OldScape-Server.
 *
 * Guthix OldScape-Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.net

class IsaacRandomPair(val encodeGen: IsaacRandom, val decodeGen: IsaacRandom)

class IsaacRandom(seed: IntArray) {
    private var valuesRemaining = 0
    private var rsl: IntArray = IntArray(256)
    private var mem: IntArray = IntArray(256)
    private var isaacA = 0
    private var isaacB = 0
    private var isaacC = 0

    init {
        for (int_0 in seed.indices) {
            rsl[int_0] = seed[int_0]
        }
        initState()
    }

    fun nextInt(): Int {
        if (--valuesRemaining + 1 == 0) {
            isaac()
            valuesRemaining = 255
        }
        return rsl[valuesRemaining]
    }

    fun isaac() {
        isaacB += ++isaacC
        for (i in 0..255) {
            val isaacX = mem[i]
            isaacA = if (i and 0x2 == 0) {
                if (i and 0x1 == 0) {
                    isaacA xor (isaacA shl 13)
                } else {
                    isaacA xor isaacA.ushr(6)
                }
            } else if (i and 0x1 == 0) {
                isaacA xor (isaacA shl 2)
            } else {
                isaacA xor isaacA.ushr(16)
            }
            isaacA += mem[128 + i and 0xFF]
            mem[i] = mem[isaacX and 0x3FC shr 2] + isaacB + isaacA
            isaacB = mem[mem[i] shr 8 and 0x3FC shr 2] + isaacX
            rsl[i] = isaacB
        }
    }

    private fun initState() {
        val arr = IntArray(8) { GOLDEN_RATIO }
        for (i in 0 until 4) shuffle(arr)
        for (i in 0 until 256 step 8) {
            arr[7] += rsl[i]
            arr[6] += rsl[i + 1]
            arr[5] += rsl[i + 2]
            arr[4] += rsl[i + 3]
            arr[3] += rsl[i + 4]
            arr[2] += rsl[i + 5]
            arr[1] += rsl[i + 6]
            arr[0] += rsl[i + 7]
            shuffle(arr)
            setState(i, arr)
        }
        for (i in 0 until 256 step 8) {
            arr[7] += mem[i]
            arr[6] += mem[i + 1]
            arr[5] += mem[i + 2]
            arr[4] += mem[i + 3]
            arr[3] += mem[i + 4]
            arr[2] += mem[i + 5]
            arr[1] += mem[i + 6]
            arr[0] += mem[i + 7]
            shuffle(arr)
            setState(i, arr)
        }
        isaac()
        valuesRemaining = 256
    }

    private fun shuffle(arr: IntArray) {
        arr[7] = arr[7] xor (arr[6] shl 11)
        arr[4] += arr[7]
        arr[6] += arr[5]
        arr[6] = arr[6] xor arr[5].ushr(2)
        arr[3] += arr[6]
        arr[5] += arr[4]
        arr[5] = arr[5] xor (arr[4] shl 8)
        arr[2] += arr[5]
        arr[4] += arr[3]
        arr[4] = arr[4] xor arr[3].ushr(16)
        arr[1] += arr[4]
        arr[3] += arr[2]
        arr[3] = arr[3] xor (arr[2] shl 10)
        arr[0] += arr[3]
        arr[2] += arr[1]
        arr[2] = arr[2] xor arr[1].ushr(4)
        arr[7] += arr[2]
        arr[1] += arr[0]
        arr[1] = arr[1] xor (arr[0] shl 8)
        arr[6] += arr[1]
        arr[0] += arr[7]
        arr[0] = arr[0] xor arr[7].ushr(9)
        arr[5] += arr[0]
        arr[7] += arr[6]
    }

    private fun setState(start: Int, arr: IntArray) {
        mem[start] = arr[7]
        mem[start + 1] = arr[6]
        mem[start + 2] = arr[5]
        mem[start + 3] = arr[4]
        mem[start + 4] = arr[3]
        mem[start + 5] = arr[2]
        mem[start + 6] = arr[1]
        mem[start + 7] = arr[0]
    }

    companion object {
        const val GOLDEN_RATIO: Int = -1640531527
    }
}
