package org.javafreedom.kcd.adapters.rest.model

import kotlinx.serialization.Serializable
import org.javafreedom.kcd.adapters.persistence.cassandra.mapper.NumberSerializer // TODO should not be used in here
import org.javafreedom.kcd.ktor.commons.UUIDSerializer
import org.javafreedom.kcd.ktor.commons.ZonedDateTimeSerializer
import java.time.ZonedDateTime
import java.util.*

@Serializable
data class Quantity(
    val unit: String,
    @Serializable(with = NumberSerializer::class) val amount: Number
)

@Serializable
data class Element(
    val type: String,
    val comment: String? = null,
    val quantity: Quantity,
    val device: String,
    val extension: String
)

@Serializable
data class Component(
    val type: String,
    val comment: String? = null,
    val elements: Collection<Element>
)

@Serializable
data class Observation(
    @Serializable(with = UUIDSerializer::class) val id: UUID? = null,
    @Serializable(with = ZonedDateTimeSerializer::class) val dateOfObservation: ZonedDateTime,
    val type: String, val component: Component
)


@Serializable
data class RequestObservation(val value: Observation)

@Serializable
data class Failure(val code: Int? = null, val message: String, val trace: String? = null)
