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

import io.guthix.buffer.readVarInt
import io.guthix.buffer.writeVarInt
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.io.IOException

public class MidiFile(public val midi: ByteArray) {
    public companion object {
        // Headers
        private const val MTHD_MAGIC = 1297377380
        private const val MTRK_MAGIC = 1297379947

        // Major MIDI Messages. Bottom 4 bits are the channel.
        private const val NOTE_ON = 144
        private const val NOTE_OFF = 128
        private const val CONTROL_CHANGE = 176
        private const val PITCH_WHEEL_CHANGE = 224
        private const val CHANNEL_PRESSURE = 208
        private const val POLYPHONIC_KEY_PRESSURE = 160
        private const val PROGRAM_CHANGE = 192

        // Meta Events
        private const val META = 255
        private const val END_OF_TRACK = 47
        private const val TEMPO = 81

        private const val JAG_NOTE_ON = 0
        private const val JAG_NOTE_OFF = 1
        private const val JAG_CONTROL_CHANGE = 2
        private const val JAG_PITCH_BEND = 3
        private const val JAG_CHANNEL_PRESSURE = 4
        private const val JAG_POLY_PRESSURE = 5
        private const val JAG_PROGRAM_CHANGE = 6
        private const val JAG_END_OF_TRACK = 7
        private const val JAG_TEMPO = 23

        // Controller messages
        private const val CONTROLLER_BANK_SELECT = 0
        private const val CONTROLLER_MODULATION_WHEEL = 1
        private const val CONTROLLER_CHANNEL_VOLUME = 7
        private const val CONTROLLER_PAN = 10
        private const val CONTROLLER_BANK_SELECT_2 = 32
        private const val CONTROLLER_MODULATION_WHEEL2 = 33
        private const val CONTROLLER_CHANNEL_VOLUME_2 = 39
        private const val CONTROLLER_PAN_2 = 42
        private const val CONTROLLER_DAMPER_PEDAL = 64
        private const val CONTROLLER_PORTAMENTO = 65
        private const val CONTROLLER_NON_REGISTERED_PARAMETER_NUMBER_LSB = 98
        private const val CONTROLLER_NON_REGISTERED_PARAMETER_NUMBER_MSB = 99
        private const val CONTROLLER_REGISTERED_PARAMETER_NUMBER_LSB = 100
        private const val CONTROLLER_REGISTERED_PARAMETER_NUMBER_MSB = 101
        private const val CONTROLLER_ALL_SOUND_OFF = 120
        private const val CONTROLLER_RESET_ALL_CONTROLLERS = 121
        private const val CONTROLLER_ALL_NOTES_OFF = 123

        public fun decode(data: ByteBuf): MidiFile {
            data.readerIndex(data.writerIndex() - 3)
            val tracks = data.readUnsignedByte().toInt()
            val division = data.readUnsignedShort()
            var offset = 14 + tracks * 10
            data.readerIndex(0)
            var tempoOpcodes = 0
            var ctrlChangeOpcodes = 0
            var noteOnOpcodes = 0
            var noteOffOpcodes = 0
            var wheelChangeOpcodes = 0
            var chnnlAfterTchOpcodes = 0
            var keyAfterTchOpcodes = 0
            var progmChangeOpcodes = 0
            for(track in 0 until tracks) {
                var opcode = -1
                while (true) {
                    val controlChangeIndex = data.readUnsignedByte().toInt()
                    if (controlChangeIndex != opcode) offset++
                    opcode = controlChangeIndex and 0xF
                    if (controlChangeIndex == JAG_END_OF_TRACK) break
                    when {
                        controlChangeIndex == JAG_TEMPO -> tempoOpcodes++
                        opcode == JAG_NOTE_ON -> noteOnOpcodes++
                        opcode == JAG_NOTE_OFF -> noteOffOpcodes++
                        opcode == JAG_CONTROL_CHANGE -> ctrlChangeOpcodes++
                        opcode == JAG_PITCH_BEND -> wheelChangeOpcodes++
                        opcode == JAG_CHANNEL_PRESSURE -> chnnlAfterTchOpcodes++
                        opcode == JAG_POLY_PRESSURE -> keyAfterTchOpcodes++
                        opcode == JAG_PROGRAM_CHANGE -> progmChangeOpcodes++
                        else -> throw IOException("Track opcode $opcode not implemented.")
                    }
                }
            }

            offset += 5 * tempoOpcodes
            offset += 2 * (noteOnOpcodes + noteOffOpcodes + ctrlChangeOpcodes + wheelChangeOpcodes + keyAfterTchOpcodes)
            offset += chnnlAfterTchOpcodes + progmChangeOpcodes
            val marker1 = data.readerIndex()
            val opcode = tracks + tempoOpcodes + ctrlChangeOpcodes + noteOnOpcodes + noteOffOpcodes +
                    wheelChangeOpcodes + chnnlAfterTchOpcodes + keyAfterTchOpcodes + progmChangeOpcodes

            for(i in 0 until opcode) {
                data.readVarInt()
            }
            offset += data.readerIndex() - marker1
            var controlChangeIndex = data.readerIndex()
            var modulationWheelSize = 0
            var modulationWheel2Size = 0
            var channelVolumeSize = 0
            var channelVolume2Size = 0
            var panSize = 0
            var pan2Size = 0
            var nonRegisteredMsbSize = 0
            var nonRegisteredLsbSize = 0
            var registeredNumberMsb = 0
            var registeredLsbSize = 0
            var commandsSize = 0
            var otherSize = 0
            var controllerNumber = 0
            for(i in 0 until ctrlChangeOpcodes) {
                controllerNumber = controllerNumber + data.readByte() and Byte.MAX_VALUE.toInt()
                if (controllerNumber == CONTROLLER_BANK_SELECT || controllerNumber == CONTROLLER_BANK_SELECT_2) {
                    progmChangeOpcodes++
                } else if (controllerNumber == CONTROLLER_MODULATION_WHEEL) {
                    modulationWheelSize++
                } else if (controllerNumber == CONTROLLER_MODULATION_WHEEL2) {
                    modulationWheel2Size++
                } else if (controllerNumber == CONTROLLER_CHANNEL_VOLUME) {
                    channelVolumeSize++
                } else if (controllerNumber == CONTROLLER_CHANNEL_VOLUME_2) {
                    channelVolume2Size++
                } else if (controllerNumber == CONTROLLER_PAN) {
                    panSize++
                } else if (controllerNumber == CONTROLLER_PAN_2) {
                    pan2Size++
                } else if (controllerNumber == CONTROLLER_NON_REGISTERED_PARAMETER_NUMBER_MSB) {
                    nonRegisteredMsbSize++
                } else if (controllerNumber == CONTROLLER_NON_REGISTERED_PARAMETER_NUMBER_LSB) {
                    nonRegisteredLsbSize++
                } else if (controllerNumber == CONTROLLER_REGISTERED_PARAMETER_NUMBER_MSB) {
                    registeredNumberMsb++
                } else if (controllerNumber == CONTROLLER_REGISTERED_PARAMETER_NUMBER_LSB) {
                    registeredLsbSize++
                } else if (controllerNumber != CONTROLLER_DAMPER_PEDAL
                    && controllerNumber != CONTROLLER_PORTAMENTO
                    && controllerNumber != CONTROLLER_ALL_SOUND_OFF
                    && controllerNumber != CONTROLLER_RESET_ALL_CONTROLLERS
                    && controllerNumber != CONTROLLER_ALL_NOTES_OFF
                ) {
                    otherSize++
                } else {
                    commandsSize++
                }
            }

            var commandsIndex = data.readerIndex()
            data.skipBytes(commandsSize)
            var polyPressureIndex = data.readerIndex()
            data.skipBytes(keyAfterTchOpcodes)
            var channelPressureIndex = data.readerIndex()
            data.skipBytes(chnnlAfterTchOpcodes)
            var pitchWheelHighIndex = data.readerIndex()
            data.skipBytes(wheelChangeOpcodes)
            var modulationWheelOffset = data.readerIndex()
            data.skipBytes(modulationWheelSize)
            var channelVolumeOffset = data.readerIndex()
            data.skipBytes(channelVolumeSize)
            var panOffset = data.readerIndex()
            data.skipBytes(panSize)
            var notesIndex = data.readerIndex()
            data.skipBytes(noteOnOpcodes + noteOffOpcodes + keyAfterTchOpcodes)
            var notesOnIndex = data.readerIndex()
            data.skipBytes(noteOnOpcodes)
            var otherIndex = data.readerIndex()
            data.skipBytes(otherSize)
            var notesOffIndex = data.readerIndex()
            data.skipBytes(noteOffOpcodes)
            var modulationWheel2Offset = data.readerIndex()
            data.skipBytes(modulationWheel2Size)
            var channelVolume2Offset = data.readerIndex()
            data.skipBytes(channelVolume2Size)
            var pan2Offset = data.readerIndex()
            data.skipBytes(pan2Size)
            var programChangeIndex = data.readerIndex()
            data.skipBytes(progmChangeOpcodes)
            var pitchWheelLowIndex = data.readerIndex()
            data.skipBytes(wheelChangeOpcodes)
            var nonRegisteredMsbIndex = data.readerIndex()
            data.skipBytes(nonRegisteredMsbSize)
            var nonRegisteredLsbIndex = data.readerIndex()
            data.skipBytes(nonRegisteredLsbSize)
            var registeredMsbIndex = data.readerIndex()
            data.skipBytes(registeredNumberMsb)
            var registeredLsbIndex = data.readerIndex()
            data.skipBytes(registeredLsbSize)
            var tempoOffset = data.readerIndex()
            data.skipBytes(tempoOpcodes * 3)

            val midiBuff = Unpooled.buffer(offset + 1)
            midiBuff.writeInt(MTHD_MAGIC) // MThd header
            midiBuff.writeInt(6) // length of header
            midiBuff.writeShort(if (tracks > 1) 1 else 0) // format
            midiBuff.writeShort(tracks) // tracks
            midiBuff.writeShort(division) // division
            data.readerIndex(marker1)
            var var52 = 0
            var var53 = 0
            var var54 = 0
            var var55 = 0
            var var56 = 0
            var var57 = 0
            var var58 = 0
            val var59 = IntArray(128)
            controllerNumber  = 0
            var var29 = 0
            writer@ for (var60 in 0 until tracks) {
                midiBuff.writeInt(MTRK_MAGIC)
                midiBuff.skipBytes(4) // length gets written here later
                val var61 = midiBuff.readerIndex()
                var curJagOpcode = -1

                while (true) {
                    val deltaTick = data.readVarInt()
                    midiBuff.writeVarInt(deltaTick)
                    val status = data.getUnsignedByte(var29++).toInt()
                    val shouldWriteOpcode = status != curJagOpcode
                    curJagOpcode = status and 0xF
                    if (status == JAG_END_OF_TRACK) {
                        midiBuff.writeByte(META)
                        midiBuff.writeByte(END_OF_TRACK) // type - end of track
                        midiBuff.writeByte(0) // length
                        midiBuff.writeLength(midiBuff.readerIndex() - var61)
                        continue@writer
                    }
                    if (status == JAG_TEMPO) {
                        midiBuff.writeByte(META) // meta event FF
                        midiBuff.writeByte(TEMPO) // type - set tempo
                        midiBuff.writeByte(3) // length
                        midiBuff.writeByte(data.getByte(tempoOffset++).toInt())
                        midiBuff.writeByte(data.getByte(tempoOffset++).toInt())
                        midiBuff.writeByte(data.getByte(tempoOffset++).toInt())
                    } else {
                        var52 = var52 xor (status shr 4)
                        when(curJagOpcode) {
                            JAG_NOTE_ON -> {
                                if (shouldWriteOpcode) midiBuff.writeByte(NOTE_ON + var52)
                                var53 += data.getByte(notesIndex++).toInt()
                                var54 += data.getByte(notesOnIndex++).toInt()
                                midiBuff.writeByte(var53 and Byte.MAX_VALUE.toInt())
                                midiBuff.writeByte(var54 and Byte.MAX_VALUE.toInt())
                            }
                            JAG_NOTE_OFF -> {
                                if (shouldWriteOpcode) midiBuff.writeByte(NOTE_OFF + var52)
                                var53 += data.getByte(notesIndex++).toInt()
                                var55 += data.getByte(notesOffIndex++).toInt()
                                midiBuff.writeByte(var53 and Byte.MAX_VALUE.toInt())
                                midiBuff.writeByte(var55 and Byte.MAX_VALUE.toInt())
                            }
                            JAG_CONTROL_CHANGE -> {
                                if (shouldWriteOpcode) {
                                    midiBuff.writeByte(CONTROL_CHANGE + var52)
                                }
                                controllerNumber = controllerNumber + data.getByte(controlChangeIndex++) and
                                        Byte.MAX_VALUE.toInt()
                                midiBuff.writeByte(controllerNumber)
                                val result = if(controllerNumber == CONTROLLER_BANK_SELECT
                                    || controllerNumber == CONTROLLER_BANK_SELECT_2) {
                                    data.getByte(programChangeIndex++)
                                } else if (controllerNumber == CONTROLLER_MODULATION_WHEEL) {
                                    data.getByte(modulationWheelOffset++)
                                } else if (controllerNumber == CONTROLLER_MODULATION_WHEEL2) {
                                    data.getByte(modulationWheel2Offset++)
                                } else if (controllerNumber == CONTROLLER_CHANNEL_VOLUME) {
                                    data.getByte(channelVolumeOffset++)
                                } else if (controllerNumber == CONTROLLER_CHANNEL_VOLUME_2) {
                                    data.getByte(channelVolume2Offset++)
                                } else if (controllerNumber == CONTROLLER_PAN) {
                                    data.getByte(panOffset++)
                                } else if (controllerNumber == CONTROLLER_PAN_2) {
                                    data.getByte(pan2Offset++)
                                } else if (controllerNumber == CONTROLLER_NON_REGISTERED_PARAMETER_NUMBER_MSB) {
                                    data.getByte(nonRegisteredMsbIndex++)
                                } else if (controllerNumber == CONTROLLER_NON_REGISTERED_PARAMETER_NUMBER_LSB) {
                                    data.getByte(nonRegisteredLsbIndex++)
                                } else if (controllerNumber == CONTROLLER_REGISTERED_PARAMETER_NUMBER_MSB) {
                                    data.getByte(registeredMsbIndex++)
                                } else if (controllerNumber == CONTROLLER_REGISTERED_PARAMETER_NUMBER_LSB) {
                                    data.getByte(registeredLsbIndex++)
                                } else if (
                                    controllerNumber != CONTROLLER_DAMPER_PEDAL &&
                                    controllerNumber != CONTROLLER_PORTAMENTO &&
                                    controllerNumber != CONTROLLER_ALL_SOUND_OFF &&
                                    controllerNumber != CONTROLLER_RESET_ALL_CONTROLLERS &&
                                    controllerNumber != CONTROLLER_ALL_NOTES_OFF
                                ) {
                                    data.getByte(otherIndex++)
                                } else {
                                    data.getByte(commandsIndex++)
                                }

                                val var67 = result + var59[controllerNumber]
                                var59[controllerNumber] = var67
                                midiBuff.writeByte(var67 and Byte.MAX_VALUE.toInt())
                            }
                            JAG_PITCH_BEND -> {
                                if (shouldWriteOpcode)  midiBuff.writeByte(PITCH_WHEEL_CHANGE + var52)
                                var56 += data.getByte(pitchWheelLowIndex++)
                                var56 += data.getByte(pitchWheelHighIndex++).toInt() shl 7
                                midiBuff.writeByte(var56 and Byte.MAX_VALUE.toInt())
                                midiBuff.writeByte(var56 shr 7 and Byte.MAX_VALUE.toInt())
                            }
                            JAG_CHANNEL_PRESSURE -> {
                                if (shouldWriteOpcode) midiBuff.writeByte(CHANNEL_PRESSURE + var52)
                                var57 += data.getByte(channelPressureIndex++)
                                midiBuff.writeByte(var57 and Byte.MAX_VALUE.toInt())
                            }
                            JAG_POLY_PRESSURE -> {
                                if (shouldWriteOpcode) midiBuff.writeByte(POLYPHONIC_KEY_PRESSURE + var52)
                                var53 += data.getByte(notesIndex++)
                                var58 += data.getByte(polyPressureIndex++)
                                midiBuff.writeByte(var53 and Byte.MAX_VALUE.toInt())
                                midiBuff.writeByte(var58 and Byte.MAX_VALUE.toInt())
                            }
                            JAG_PROGRAM_CHANGE -> {
                                if (shouldWriteOpcode) midiBuff.writeByte(PROGRAM_CHANGE + var52)
                                midiBuff.writeByte(data.getByte(programChangeIndex++).toInt())
                            } else -> throw IOException("Did not recognise jag track opcode $curJagOpcode.")
                        }
                    }
                }
            }
            return MidiFile(midiBuff.array())
        }

        private fun ByteBuf.writeLength(length: Int) {
            val pos = readerIndex()
            readerIndex(pos - length - 4)
            writeInt(length)
            readerIndex(pos)
        }
    }
}