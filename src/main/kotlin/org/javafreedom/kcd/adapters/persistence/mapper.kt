package org.javafreedom.kcd.adapters.persistence

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.javafreedom.kcd.common.ArbitraryMapSerializer
import org.javafreedom.kcd.domain.EmbeddedAudit
import org.javafreedom.kcd.domain.ObservationDetails
import java.util.*

val json = Json(JsonConfiguration.Stable)

fun org.javafreedom.kcd.domain.Observation.mapToPersistence(newId: UUID): Observation {
    val jsonData = json.stringify(ArbitraryMapSerializer, details.data)

    return Observation(
        newId, user, type, details.date, audit.createdAt, audit.modifiedAt, details.unit, jsonData
    )
}

fun Observation.mapToDomain(): org.javafreedom.kcd.domain.Observation {
    val audit = EmbeddedAudit(this.createdAt, this.modifiedAt)
    val data = json.parse(ArbitraryMapSerializer, this.data)

    val details = ObservationDetails(this.unit, this.date, data)

    return org.javafreedom.kcd.domain.Observation(this.id, this.user, this.type, audit, details)
}
