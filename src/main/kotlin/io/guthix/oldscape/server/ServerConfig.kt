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