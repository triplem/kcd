package org.javafreedom.kcd.adapters.rest.mapper

import assertk.Assert
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isEqualToIgnoringGivenProperties
import io.ktor.util.*
import org.javafreedom.kcd.adapters.rest.DomainComponent
import org.javafreedom.kcd.adapters.rest.DomainElement
import org.javafreedom.kcd.adapters.rest.DomainObservation
import org.javafreedom.kcd.adapters.rest.DomainQuantity
import org.javafreedom.kcd.domain.model.EmbeddedAudit
import org.javafreedom.kcd.domain.model.Observation
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

class MappingTest {

    @Test
    fun testRoundtripMapping_OneElement() {
        val observation = DomainObservation(
            UUID.randomUUID(), Instant.now(),
            EmbeddedAudit("user", Instant.now(), Instant.now()),
            true, DomainElement(
                "type", "comment",
                DomainQuantity("unit", 100), "device",
                "extension"
            ), null
        )

        val mapped = observation.mapToRest()
        val backMapped = mapped.mapToDomain("user", null)

        assertThat(backMapped).isEqualToIgnoringGivenProperties(
            observation,
            DomainObservation::date, DomainObservation::audit
        )
        assertThat(backMapped.date).isEqualWithoutMillis(observation.date)
        assertThat(backMapped.audit.user).isEqualTo(observation.audit.user)
        assertThat(backMapped.audit.createdAt).isEqualWithoutMillis(observation.audit.createdAt)
        assertThat(backMapped.audit.modifiedAt).isEqualWithoutMillis(observation.audit.modifiedAt)
    }

    @Test
    fun testRoundtripMapping_MultipleElements() {
        val observation = DomainObservation(
            UUID.randomUUID(), Instant.now(),
            EmbeddedAudit("user", Instant.now(), Instant.now()),
            true, null,
            DomainComponent(
                "type", "comment",
                listOf(
                    DomainElement(
                        "type", "comment",
                        DomainQuantity("unit", 100), "device",
                        "extension"
                    ),
                    DomainElement(
                        "type", "comment1",
                        DomainQuantity("unit", 100), "device1",
                        "extension"
                    )
                )
            )
        )

        val mapped = observation.mapToRest()
        val backMapped = mapped.mapToDomain("user", null)

        assertThat(backMapped).isEqualToIgnoringGivenProperties(
            observation,
            DomainObservation::date, DomainObservation::audit
        )
        assertThat(backMapped.date).isEqualWithoutMillis(observation.date)
        assertThat(backMapped.audit.user).isEqualTo(observation.audit.user)
        assertThat(backMapped.audit.createdAt).isEqualWithoutMillis(observation.audit.createdAt)
        assertThat(backMapped.audit.modifiedAt).isEqualWithoutMillis(observation.audit.modifiedAt)
    }

    @Test
    fun testRestMapping_Error() {
        val observation = Observation(null, Instant.now(),
            EmbeddedAudit("user", Instant.now(), Instant.now()),
            true,
            null, null)

        val exception = assertThrows<DataConversionException>("element or component should be filled") {
            observation.mapToRest()
        }

        assertThat(exception.message).isEqualTo("element or component should be filled")
    }

    private fun Assert<Instant>.isEqualWithoutMillis(expected: Instant) = given { actual ->
        actual.truncatedTo(ChronoUnit.SECONDS) === expected.truncatedTo(ChronoUnit.SECONDS)
    }

}