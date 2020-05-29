package org.javafreedom.kcd.ktor.commons

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Serializer(forClass = ZonedDateTime::class)
object ZonedDateTimeSerializer : KSerializer<ZonedDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ZonedDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ZonedDateTime) {
        encoder.encodeString(
            value.truncatedTo(ChronoUnit.MILLIS).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        )
    }

    override fun deserialize(decoder: Decoder): ZonedDateTime {
        return ZonedDateTime.parse(decoder.decodeString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }
}
