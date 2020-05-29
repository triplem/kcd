@file:UseSerializers(ZonedDateTimeSerializer::class, UUIDSerializer::class)
package org.javafreedom.kcd.adapters.rest

import kotlinx.serialization.*
import kotlinx.serialization.json.JsonElement
import org.javafreedom.kcd.common.UUIDSerializer
import org.javafreedom.kcd.common.ZonedDateTimeSerializer
import java.time.ZonedDateTime
import java.util.*

@Serializable
data class Observation(val id: UUID?, val type: String, val unit: String, val date: ZonedDateTime,
                       val data: JsonElement)

@Serializable
data class RequestDataPoint(val value: Observation)

@Serializable
data class Failure(val message: String)
