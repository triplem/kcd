@file:UseSerializers(ZonedDateTimeSerializer::class)
package org.javafreedom.kcd.adapters.rest.deprecated

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import mu.KotlinLogging
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.reflect.full.findAnnotation

private val logger = KotlinLogging.logger {}

@Serializable
class SensorDetails(val location: String = "",
                    val manufacturer: String = "")

@Serializable
@Polymorphic
abstract class Value {
    @Required
    abstract val unit: String
    abstract val displayValue: String
    @Required
    abstract val date: ZonedDateTime
}

@SerialName("single")
@Serializable
data class SingleValues(override val unit: String, override val displayValue: String,
                        override val date: ZonedDateTime, val value: String): Value()

fun SingleValues.mapTo() = SingleDomainValue(unit, displayValue, date.toInstant(), value)

@SerialName("multi")
@Serializable
data class TwoValues(override val unit: String, override val displayValue: String,
                     override val date: ZonedDateTime, val lowerValue: String, val upperValue: String): Value()

abstract class DomainValue {
    abstract val unit: String
    abstract val displayValue: String
    abstract val date: Instant
}

data class SingleDomainValue(override val unit: String, override val displayValue: String,
                             override val date: Instant, val value: String): DomainValue()

data class TwoDomainValue(override val unit: String, override val displayValue: String,
                          override val date: Instant, val lowerValue: String,
                          val upperValue: String): DomainValue()

fun TwoValues.mapTo() = TwoDomainValue(unit, displayValue, date.toInstant(), lowerValue, upperValue)

val serializerModule  = SerializersModule { // 1
    polymorphic(Value::class) { // 2
        SingleValues::class with SingleValues.serializer() // 3
        TwoValues::class with TwoValues.serializer() // 4
    }
}

@Serializable
class RequestDataPoint(val sensorDetails: SensorDetails, @Polymorphic val value: Value)

class DomainDataPoint(val uuid: UUID, val date: Instant, val createdAt: Instant, val modifiedAt: Instant,
                      val user: String, val type: String, val value: DomainValue)

fun Value.serialName() = this::class.findAnnotation<SerialName>()?.let { it.value } ?: ""

fun RequestDataPoint.mapTo(user: String, json: Json): DomainDataPoint {

    val date = Instant.now()
    logger.debug("daate: {}", date)

    val discriminator = this.value.serialName()
    logger.debug("discriminator: {}", discriminator)

    // TODO this is not nice, we do need to find another solution, which does not require additional coding outside of the
    // model class(es)
    val domainValue = when(value) {
        is SingleValues -> value.mapTo()
        is TwoValues -> value.mapTo()
        else -> throw MappingException()
    }

    return DomainDataPoint(
        UUID.randomUUID(), this.value.date.toInstant(), date, date, user,
        discriminator, domainValue)
}

@Serializer(forClass = ZonedDateTime::class)
object ZonedDateTimeSerializer : KSerializer<ZonedDateTime> {
    override val descriptor: SerialDescriptor = PrimitiveDescriptor("ZonedDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, obj: ZonedDateTime) {
        encoder.encodeString(obj.truncatedTo(ChronoUnit.MILLIS).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
    }

    override fun deserialize(decoder: Decoder): ZonedDateTime {
        return ZonedDateTime.parse(decoder.decodeString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }
}

class MappingException : RuntimeException()
