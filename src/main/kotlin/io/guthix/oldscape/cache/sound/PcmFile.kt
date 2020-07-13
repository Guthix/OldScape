/*
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
 * along with Guthix OldScape-Cache. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.cache.sound

import io.guthix.buffer.readSmallSmart
import io.guthix.buffer.readUnsignedSmallSmart
import io.netty.buffer.ByteBuf

public class SoundEffect(public val start: Int, public val end: Int, public val instruments: Array<AudioInstrument?>) {
    public companion object {
        private const val INSTRUMENT_COUNT = 10

        public fun decode(data: ByteBuf): SoundEffect {
            val instruments = arrayOfNulls<AudioInstrument>(INSTRUMENT_COUNT)
            for (i in 0 until INSTRUMENT_COUNT) {
                val volume = data.readUnsignedByte().toInt()
                if (volume != 0) {
                    data.readerIndex(data.writerIndex() - 1)
                    instruments[i] = AudioInstrument.decode(data)
                }
            }
            val start = data.readUnsignedShort()
            val end = data.readUnsignedShort()
            return SoundEffect(start, end, instruments)
        }
    }
}

public class AudioInstrument(
    public val pitch: AudioEnvelope,
    public val volume: AudioEnvelope,
    public val pitchModifier: AudioEnvelope?,
    public val pitchModifierAmplitude: AudioEnvelope?,
    public val volumeMultiplier: AudioEnvelope?,
    public val volumeMultiplierAmplitude: AudioEnvelope?,
    public val release: AudioEnvelope?,
    public val field1397: AudioEnvelope?,
    public val oscillatorVolume: IntArray,
    public val oscillatorPitch: IntArray,
    public val oscillatorDelays: IntArray,
    public val delayTime: Int,
    public val delayDecay: Int,
    public val duration: Int,
    public val offset: Int,
    public val filterEnvelope: AudioEnvelope,
    public val filter: AudioFilter
) {
    public companion object {
        private const val OSCILLATOR_COUNT = 10

        public fun decode(data: ByteBuf): AudioInstrument {
            val pitch = AudioEnvelope.decode(data)
            val volume = AudioEnvelope.decode(data)
            val (pitchModifier, pitchModifierAmplitude) = if (data.readUnsignedByte().toInt() != 0) {
                data.readerIndex(data.readerIndex() - 1)
                Pair(AudioEnvelope.decode(data), AudioEnvelope.decode(data))
            } else Pair(null, null)
            val (volumeMultiplier, volumeMultiplierAmplitude) = if (data.readUnsignedByte().toInt() != 0) {
                data.readerIndex(data.readerIndex() - 1)
                Pair(AudioEnvelope.decode(data), AudioEnvelope.decode(data))
            } else Pair(null, null)
            val (release, field1397) = if (data.readUnsignedByte().toInt() != 0) {
                data.readerIndex(data.readerIndex() - 1)
                Pair(AudioEnvelope.decode(data), AudioEnvelope.decode(data))
            } else Pair(null, null)
            val oscillatorVolume = IntArray(OSCILLATOR_COUNT)
            val oscillatorPitch = IntArray(OSCILLATOR_COUNT)
            val oscillatorDelays = IntArray(OSCILLATOR_COUNT)
            for (i in 0 until OSCILLATOR_COUNT) {
                val oscVolume = data.readUnsignedSmallSmart()
                if (oscVolume == 0) {
                    break
                }
                oscillatorVolume[i] = oscVolume
                oscillatorPitch[i] = data.readSmallSmart()
                oscillatorDelays[i] = data.readUnsignedSmallSmart()
            }

            val delayTime = data.readUnsignedSmallSmart()
            val delayDecay = data.readUnsignedSmallSmart()
            val duration = data.readUnsignedShort()
            val offset = data.readUnsignedShort()
            val filterEnvelope = AudioEnvelope()
            val filter = AudioFilter.decode(data, filterEnvelope)
            return AudioInstrument(
                pitch, volume, pitchModifier, pitchModifierAmplitude, volumeMultiplier, volumeMultiplierAmplitude,
                release, field1397, oscillatorVolume, oscillatorPitch, oscillatorDelays, delayTime, delayDecay,
                duration, offset, filterEnvelope, filter
            )
        }
    }
}

public class AudioEnvelope {
    public var form: Short? = null
    public var start: Int? = null
    public var end: Int? = null
    public var durations: IntArray? = null
    public var phases: IntArray? = null

    public fun decodeSegments(data: ByteBuf): AudioEnvelope {
        val segmentCount = data.readUnsignedByte().toInt()
        durations = IntArray(segmentCount)
        phases = IntArray(segmentCount)
        for (i in 0 until segmentCount) {
            durations!![i] = data.readUnsignedShort()
            phases!![i] = data.readUnsignedShort()
        }
        return this
    }

    public companion object {
        public fun decode(data: ByteBuf): AudioEnvelope {
            val audioEnvelope = AudioEnvelope()
            audioEnvelope.form = data.readUnsignedByte()
            audioEnvelope.start = data.readInt()
            audioEnvelope.end = data.readInt()
            return audioEnvelope.decodeSegments(data)
        }
    }
}

public class AudioFilter(
    public val pairs: IntArray,
    public val unity: IntArray,
    public val phases: Array<Array<IntArray>>,
    public val magnitudes: Array<Array<IntArray>>
) {

    public companion object {
        public const val SIZE: Int = 2

        public fun decode(data: ByteBuf, audioEnvelope: AudioEnvelope): AudioFilter {
            val pair = data.readUnsignedByte().toInt()
            val pairs = IntArray(SIZE)
            pairs[0] = pair shr 4
            pairs[1] = pair and 0xF
            val phases = Array(SIZE) { Array(SIZE) { IntArray(SIZE * 2) } }
            val magnitudes = Array(SIZE) { Array(SIZE) { IntArray(SIZE * 2) } }
            val unity = IntArray(SIZE)
            if (pair != 0) {
                unity[0] = data.readUnsignedShort()
                unity[1] = data.readUnsignedShort()
                val uByte1 = data.readUnsignedByte().toInt()
                for(i in 0 until SIZE) {
                    for(j in 0 until pairs[i]) {
                        phases[i][0][j] = data.readUnsignedShort()
                        magnitudes[i][0][j] = data.readUnsignedShort()
                    }
                }

                for(i in 0 until SIZE) {
                    for(j in 0 until pairs[i]) {
                        if (uByte1 and (1 shl i * 4 shl j) != 0) {
                            phases[i][1][j] = data.readUnsignedShort()
                            magnitudes[i][1][j] = data.readUnsignedShort()
                        } else {
                            phases[i][1][j] = phases[i][0][j]
                            magnitudes[i][1][j] = magnitudes[i][0][j]
                        }
                    }
                }

                if (uByte1 != 0 || unity[1] != unity[0]) {
                    audioEnvelope.decodeSegments(data)
                }
            } else {
                unity[1] = 0
            }
            return AudioFilter(pairs, unity, phases, magnitudes)
        }
    }
}
