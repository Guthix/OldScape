package io.guthix.oldscape.wiki.serialization

import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor
import java.time.LocalDate

@Serializer(forClass = LocalDate::class)
object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor = StringDescriptor.withName("LocalDate")

    override fun serialize(encoder: Encoder, obj: LocalDate) = encoder.encodeString(obj.toString())

    override fun deserialize(decoder: Decoder) = LocalDate.parse(decoder.decodeString())
}