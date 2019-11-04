package io.guthix.oldscape.server.net.state

import java.util.*

class IsaacRandomPair(val clientGen : IsaacRandom, val serverGen: IsaacRandom)

class IsaacRandom(private val seed: IntArray) {
    private var randResult: IntArray = IntArray(256) // result of the last generation
    private var valuesUsed = 0 // number of values already used in randResult

    // internal ISAAC random state
    private val mm: IntArray = IntArray(256)
    private var aa = 0
    private var bb = 0
    private var cc = 0

    init {
        for (i in seed.indices) {
            randResult[i] = seed[i]
        }
        init(seed)
    }

    fun nextInt(): Int {
        if (--valuesUsed + 1 == 0) {
            generateMoreResults()
            valuesUsed = 255
        }

        return randResult[valuesUsed]
    }

    fun generateMoreResults() {
        bb += ++cc
        for (i in 0 until 256) {
            val x = mm[i]
            when (i and 3) {
                0 -> aa = aa xor (aa shl 13)
                1 -> aa = aa xor aa.ushr(6)
                2 -> aa = aa xor (aa shl 2)
                3 -> aa = aa xor aa.ushr(16)
            }
            aa += mm[i xor 128]
            mm[i] = mm[x.ushr(2) and 0xFF] + aa + bb
            val y = mm[i]
            bb = mm[y.ushr(10) and 0xFF] + x
            randResult[i] = bb
        }
    }

    private fun init(seed: IntArray) {
        var mutSeed = seed
        if (seed.size != 256) {
            mutSeed = Arrays.copyOf(seed, 256)
        }
        cc = 0
        bb = cc
        aa = bb
        val initState = IntArray(8)
        Arrays.fill(initState, -0x61c88647)    // the golden ratio
        for (i in 0..3) {
            mix(initState)
        }
        for(i in 0 until 256 step 8) {
            for (j in 0 until 8) {
                initState[j] += mutSeed[i + j]
            }
            mix(initState)
            for (j in 0 until 8) {
                mm[i + j] = initState[j]
            }
        }
        for(i in 0 until 256 step 8) {
            for (j in 0 until 8) {
                initState[j] += mm[i + j]
            }
            mix(initState)
            for (j in 0 until 8) {
                mm[i + j] = initState[j]
            }
        }
        valuesUsed = 256
    }

    private fun mix(s: IntArray) {
        s[0] = s[0] xor (s[1] shl 11)
        s[3] += s[0]
        s[1] += s[2]
        s[1] = s[1] xor s[2].ushr(2)
        s[4] += s[1]
        s[2] += s[3]
        s[2] = s[2] xor (s[3] shl 8)
        s[5] += s[2]
        s[3] += s[4]
        s[3] = s[3] xor s[4].ushr(16)
        s[6] += s[3]
        s[4] += s[5]
        s[4] = s[4] xor (s[5] shl 10)
        s[7] += s[4]
        s[5] += s[6]
        s[5] = s[5] xor s[6].ushr(4)
        s[0] += s[5]
        s[6] += s[7]
        s[6] = s[6] xor (s[7] shl 8)
        s[1] += s[6]
        s[7] += s[0]
        s[7] = s[7] xor s[0].ushr(9)
        s[2] += s[7]
        s[0] += s[1]
    }

    companion object {
        const val GOLDEN_RATIO = -1640531527
    }
}
