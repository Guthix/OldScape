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
package io.guthix.oldscape.server.net.login

import io.guthix.buffer.readString0CP1252
import io.guthix.buffer.readStringCP1252
import io.guthix.cache.js5.util.XTEA_KEY_SIZE
import io.guthix.cache.js5.util.xteaDecrypt
import io.guthix.oldscape.server.net.IsaacRandom
import io.guthix.oldscape.server.net.IsaacRandomPair
import io.guthix.oldscape.server.world.entity.ClientSettings
import io.guthix.oldscape.server.world.entity.MachineSettings
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import java.io.IOException
import java.math.BigInteger

class LoginDecoder(
    private val archiveCount: Int,
    private val rsaPrivateKey: BigInteger,
    private val rsaMod: BigInteger
) : ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext, inc: ByteBuf, out: MutableList<Any>) {
        if (!inc.isReadable(3)) return
        inc.markReaderIndex()
        val loginType = LoginType.find(inc.readUnsignedByte().toInt())
        val size = inc.readUnsignedShort()
        if (!inc.isReadable(size)) {
            inc.resetReaderIndex()
            return
        }
        val revision = inc.readInt()
        if (inc.readInt() != 1) throw IOException(
            "Error while decoding login header."
        )
        inc.readByte() // param
        val encryptedSize = inc.readUnsignedShort()
        val rsaBuf = inc.decipherRsa(rsaPrivateKey, rsaMod, encryptedSize)
        val encCheck = rsaBuf.readUnsignedByte().toInt()
        if (encCheck != 1) throw IOException("RSA keys didn't match, first byte was $encCheck.")
        val encodeSeed = IntArray(XTEA_KEY_SIZE) { rsaBuf.readInt() }
        val sessionId = rsaBuf.readLong()
        val authType = rsaBuf.readUnsignedByte().toInt()
        when (authType) {
            2 -> rsaBuf.skipBytes(4)
            1, 3 -> {
                rsaBuf.readUnsignedMedium() //Authenticator code
                rsaBuf.skipBytes(1)
            }
            0 -> { //Trusted computer
                rsaBuf.readInt() //?
            }
        }
        val password = rsaBuf.readStringCP1252()
        val xteaData = inc.xteaDecrypt(encodeSeed)
        val userName = xteaData.readStringCP1252()
        val clientSettings = xteaData.decodeClientSettings()
        val uniqueId = ByteArray(24) { // unique id stored in random.dat
            xteaData.readByte()
        }
        xteaData.readStringCP1252() // param
        xteaData.readInt() // param
        val machineSettings = xteaData.decodeMachineSettings()
        xteaData.readUnsignedByte()
        xteaData.readInt()
        val crcs = IntArray(archiveCount) {
            xteaData.readInt()
        }
        val decodeSeed = IntArray(XTEA_KEY_SIZE) { encodeSeed[it] + 50 }
        val isaacPair = IsaacRandomPair(IsaacRandom(decodeSeed), IsaacRandom(encodeSeed))
        out.add(LoginRequest(loginType, revision, authType, sessionId, uniqueId, userName, password, clientSettings,
            machineSettings, crcs, isaacPair, ctx)
        )
    }

    private fun ByteBuf.decipherRsa(exp: BigInteger, mod: BigInteger, size: Int): ByteBuf {
        val bytes = ByteArray(size)
        readBytes(bytes)
        return Unpooled.wrappedBuffer(BigInteger(bytes).modPow(exp, mod).toByteArray())
    }

    private fun ByteBuf.decodeClientSettings(): ClientSettings {
        val bitPack = readUnsignedByte().toInt()
        val isResizable = bitPack >= 2
        val lowMemory = bitPack % 2 == 1
        val width = readUnsignedShort()
        val height = readUnsignedShort()
        return ClientSettings(isResizable, lowMemory, width, height)
    }

    private fun ByteBuf.decodeMachineSettings(): MachineSettings {
        val check = readUnsignedByte().toInt()
        if (check != 8) throw IOException(
            "First byte of machine settings decoding should be 8 but was $check."
        )
        val operatingSystem = MachineSettings.OperatingSystem.get(readUnsignedByte().toInt())
        val is64Bit = readUnsignedByte().toInt() == 1
        val osVersion = getOsVersion(operatingSystem, readUnsignedShort())
        val javaVendor = MachineSettings.JavaVendor.get(readUnsignedByte().toInt())
        val javaVersionMajor = readUnsignedByte().toInt()
        val javaVersionMinor = readUnsignedByte().toInt()
        val javaVersionPatch = readUnsignedByte().toInt()
        readUnsignedByte()
        val maxMemory = readUnsignedShort()
        val availableProcessors = readUnsignedByte().toInt()
        skipBytes(5)
        readString0CP1252()
        readString0CP1252()
        readString0CP1252()
        readString0CP1252()
        skipBytes(3)
        readString0CP1252()
        readString0CP1252()
        skipBytes(18)
        readString0CP1252()
        return MachineSettings(operatingSystem, is64Bit, osVersion, javaVendor, javaVersionMajor, javaVersionMinor,
            javaVersionPatch, maxMemory, availableProcessors
        )
    }

    private fun getOsVersion(os: MachineSettings.OperatingSystem, opcode: Int): String {
        return when (os) {
            MachineSettings.OperatingSystem.WINDOWS -> when (opcode) {
                1 -> "4.0"
                2 -> "4.1"
                3 -> "4.9"
                4 -> "5.0"
                5 -> "5.1"
                6 -> "6.0"
                7 -> "6.1"
                8 -> "5.2"
                9 -> "6.2"
                10 -> "6.3"
                11 -> "10.0"
                else -> "$opcode"
            }
            MachineSettings.OperatingSystem.OSX -> when (opcode) {
                20 -> "10.4"
                21 -> "10.5"
                22 -> "10.6"
                23 -> "10.7"
                24 -> "10.8"
                25 -> "10.9"
                26 -> "10.10"
                27 -> "10.11"
                28 -> "10.12"
                39 -> "10.13"
                else -> "$opcode"
            }
            else -> "$opcode"
        }
    }
}