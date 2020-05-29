package org.javafreedom.kcd.common

import kotlinx.serialization.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

@Serializer(forClass = UUID::class)
object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor = PrimitiveDescriptor("UUID", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, obj: UUID) {
        encoder.encodeString(obj.toString())
    }

    override fun deserialize(decoder: Decoder): UUID {
        return UUID.fromString(decoder.decodeString())
    }
}
