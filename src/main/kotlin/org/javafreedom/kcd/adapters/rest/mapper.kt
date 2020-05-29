package org.javafreedom.kcd.adapters.rest

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.javafreedom.kcd.adapters.persistence.json
import org.javafreedom.kcd.adapters.persistence.mapToDomain
import org.javafreedom.kcd.common.ArbitraryMapSerializer
import org.javafreedom.kcd.common.toZonedDateTime
import org.javafreedom.kcd.domain.EmbeddedAudit
import java.time.Instant
import java.util.*

fun Observation.mapToDomain(user: String, uuid: UUID?): org.javafreedom.kcd.domain.Observation {
    val auditDate = Instant.now()

    val embeddedAudit = EmbeddedAudit(auditDate, auditDate)
    val jsonParser = Json(JsonConfiguration.Stable)
    val data = jsonParser.fromJson(ArbitraryMapSerializer, data)
    val domainValue = org.javafreedom.kcd.domain.ObservationDetails(unit, date.toInstant(), data)

    return org.javafreedom.kcd.domain.Observation(uuid, user, type, embeddedAudit, domainValue)
}

fun org.javafreedom.kcd.domain.Observation.mapToRest(): Observation {
    val jsonParser = Json(JsonConfiguration.Stable)

    val data = jsonParser.toJson(ArbitraryMapSerializer, this.details.data)

    return Observation(
        this.id, this.type, this.details.unit, this.details.date.toZonedDateTime(), data)
}
