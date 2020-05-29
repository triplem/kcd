package org.javafreedom.kcd.adapters.persistence.cassandra.mapper

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.javafreedom.kcd.adapters.persistence.*
import java.time.Instant
import java.util.*
import kotlin.test.Test

class MappingTest {

    @Test
    fun roundTripPersistenceMapping() {
        val domainObservation = createObservation()

        val persistenceObservation = domainObservation.mapToPersistence()
        val mappedBack = persistenceObservation.mapToDomain()

        assertThat(domainObservation).isEqualTo(mappedBack)
    }

    private fun createObservation(): DomainObservation {
        val id = UUID.randomUUID()
        val now = Instant.now()

        val audit = DomainEmbeddedAudit("user", now, now)
        val quantity = DomainQuantity("unit", 100)
        val element = DomainElement(
            "type", "element", quantity, "device",
            "extension"
        )

        val quantityNested1 = DomainQuantity("unit", 100.0)
        val nestedElement1 = DomainElement(
            "type", "nestedElement1", quantityNested1,
            "device", "extension"
        )
        val quantityNested2 = DomainQuantity("unit", 10)
        val nestedElement2 = DomainElement(
            "type", "nestedElement2", quantityNested2,
            "device", "extension"
        )

        val component = DomainComponent("type", "component", listOf(nestedElement1, nestedElement2))

        return DomainObservation(id, now, audit, true, element, component)
    }
}