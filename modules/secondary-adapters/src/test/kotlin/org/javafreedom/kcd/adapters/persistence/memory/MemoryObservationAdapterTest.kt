package org.javafreedom.kcd.adapters.persistence.memory

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import kotlinx.coroutines.test.runBlockingTest
import org.javafreedom.kcd.adapters.persistence.DomainElement
import org.javafreedom.kcd.adapters.persistence.DomainEmbeddedAudit
import org.javafreedom.kcd.adapters.persistence.DomainObservation
import org.javafreedom.kcd.adapters.persistence.cassandra.repository.NoDataForQueryFound
import org.javafreedom.kcd.domain.model.Quantity
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

class MemoryObservationAdapterTest {

    @Test
    fun testInsert() = runBlockingTest {
        val sut = MemoryObservationAdapter()

        val uuid = UUID.randomUUID()
        val observation = createObservation(uuid)

        assertThat(sut.saveObservation(observation)).isEqualTo(uuid)
        assertThat(sut.dataHolder).hasSize(1)
    }

    @Test
    fun testFindObservationsBetween_OK() = runBlockingTest {
        val sut = MemoryObservationAdapter()

        for (i in 1..10) {
            val observation = createObservation(suffix = "_$i")
            sut.saveObservation(observation)
        }

        val result = sut.findObservationsBetween(
            "user_1",
            Instant.now().minus(1, ChronoUnit.DAYS),
            Instant.now().plus(1, ChronoUnit.DAYS)
        )

        assertThat(result).hasSize(1)
    }

    @Test
    fun testFindObservationsByTypeAndBetween_OK() = runBlockingTest {
        val sut = MemoryObservationAdapter()

        for (i in 1..10) {
            val observation = createObservation(suffix = "_$i")
            sut.saveObservation(observation)
        }

        var result = sut.findObservationsByTypeAndBetween(
            "user_1", "type_1",
            Instant.now().minus(1, ChronoUnit.DAYS),
            Instant.now().plus(1, ChronoUnit.DAYS)
        )

        assertThat(result).hasSize(1)

        for (i in 1..10) {
            val observation = createObservation(suffix = "_$i")
            sut.saveObservation(observation)
        }

        result = sut.findObservationsByTypeAndBetween(
            "user_1", "type_1",
            Instant.now().minus(1, ChronoUnit.DAYS),
            Instant.now().plus(1, ChronoUnit.DAYS)
        )

        assertThat(result).hasSize(2)
    }

    @Test
    fun testFindObservationsByTypeAndBetween_NOK() = runBlockingTest {
        val sut = MemoryObservationAdapter()

        for (i in 1..10) {
            val observation = createObservation(suffix = "_$i")
            sut.saveObservation(observation)
        }

        assertThrows<NoDataForQueryFound> {
            sut.findObservationsByTypeAndBetween(
                "user_1", "type_11",
                Instant.now().minus(1, ChronoUnit.DAYS),
                Instant.now().plus(1, ChronoUnit.DAYS)
            )
        }
    }

    private fun createObservation(
        uuid: UUID = UUID.randomUUID(),
        suffix: String = ""
    ): DomainObservation {
        val embeddedAudit = DomainEmbeddedAudit("user$suffix", Instant.now(), Instant.now())
        val element = DomainElement(
            "type$suffix", null, Quantity("test", 100),
            "device", "extension"
        )
        return DomainObservation(uuid, Instant.now(), embeddedAudit, true, element, null)
    }

}
