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
package io.guthix.oldscape.server

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigInteger

@Serializable
data class ServerConfig(val revision: Int, val port: Int, val rsa: RSA, val db: DB) {
    @Serializable
    data class RSA(
        @Serializable(with = BigIntegerSerializer::class) val publicKey: BigInteger,
        @Serializable(with = BigIntegerSerializer::class) val privateKey: BigInteger,
        @Serializable(with = BigIntegerSerializer::class) val modulus: BigInteger
    )

    @Serializable
    data class DB(
        val username: String,
        val password: String,
        val url: String
    )
}


object BigIntegerSerializer : KSerializer<BigInteger> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BigInteger", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: BigInteger): Unit = encoder.encodeString(value.toString(10))

    override fun deserialize(decoder: Decoder): BigInteger = BigInteger(decoder.decodeString())
}