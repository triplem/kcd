package org.javafreedom.kcd.adapters.rest

import org.javafreedom.kcd.domain.EmbeddedAudit
import org.javafreedom.kcd.domain.ObservationDetails
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals

class MappingTest {

    @Test
    fun roundTripPersistenceMapping() {
        val id = UUID.randomUUID()
        val now = Instant.now()

        val audit = EmbeddedAudit(now, now)
        val details = ObservationDetails("unit", now, mapOf("string" to "value", "number" to 2L,
            "map" to mapOf("string" to "subvalue", "number" to 6.4)))

        val observation =
            org.javafreedom.kcd.domain.Observation(id, "user", "type", audit, details)

        val restObservation = observation.mapToRest()
        val domainObservation = restObservation.mapToDomain("user", id)

        assertEquals(observation.id, domainObservation.id)
        assertEquals(observation.details.unit, domainObservation.details.unit)
        assertEquals(observation.details.data, domainObservation.details.data)

        assertEquals(observation, domainObservation)
    }
}