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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server

import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor
import java.math.BigInteger

@Serializable
data class ServerConfig(val revision: Int, val port: Int, val rsa: RSA)

@Serializable
data class RSA(
    @Serializable(BigIntegerSerializer::class) val publicKey: BigInteger,
    @Serializable(BigIntegerSerializer::class) val privateKey: BigInteger,
    @Serializable(BigIntegerSerializer::class) val modulus: BigInteger
)

@Serializer(forClass = BigInteger::class)
object BigIntegerSerializer : KSerializer<BigInteger> {
    override val descriptor: SerialDescriptor = StringDescriptor.withName("BigInteger")

    override fun serialize(encoder: Encoder, obj: BigInteger) = encoder.encodeString(obj.toString())

    override fun deserialize(decoder: Decoder) = BigInteger(decoder.decodeString())
}