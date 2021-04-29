package org.javafreedom.kcd.adapters.persistence.cassandra

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.javafreedom.kcd.adapters.persistence.cassandra.repository.NoDataForQueryFound
import org.javafreedom.kcd.adapters.persistence.cassandra.repository.ObservationRepository
import org.javafreedom.kcd.domain.model.Element
import org.javafreedom.kcd.domain.model.EmbeddedAudit
import org.javafreedom.kcd.domain.model.Observation
import org.javafreedom.kcd.domain.model.Quantity

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Instant
import java.util.*

@ExperimentalCoroutinesApi
@ExtendWith(MockKExtension::class)
internal class CassandraObservationAdapterTest {

    @MockK
    lateinit var repository: ObservationRepository

    @Test
    fun deleteObservation() {
        coEvery { repository.delete(any(), any()) } returns true
        val sut = CassandraObservationAdapter(repository)

        runBlockingTest {
            assertThat(sut.deleteObservation("user", UUID.randomUUID())).isTrue()
        }
    }

    @Test
    fun findObservation() {
        val repoObservation = createPersistenceObservation()
        coEvery { repository.find(any(), any()) } returns repoObservation
        val sut = CassandraObservationAdapter(repository)

        runBlockingTest {
            assertThat(sut.findObservation("user", UUID.randomUUID())).isNotNull()
        }
    }

    @Test
    fun findObservation_null() {
        coEvery { repository.find(any(), any()) } throws NoDataForQueryFound("")
        val sut = CassandraObservationAdapter(repository)

        runBlockingTest {
            assertThat {
                sut.findObservation("user", UUID.randomUUID())
            }.isFailure().isInstanceOf(NoDataForQueryFound::class)
        }
    }

    @Test
    fun saveObservation_WithoutId() {
        coEvery { repository.upsert(any()) } just runs
        val sut = CassandraObservationAdapter(repository)

        runBlockingTest {
            assertThat(sut.saveObservation(createObservationWithoutId())).isNotNull()
        }
    }

    @Test
    fun saveObservation_WithId() {
        coEvery { repository.upsert(any()) } just runs
        val sut = CassandraObservationAdapter(repository)
        val id = UUID.randomUUID()
        val observation = createObservationWithoutId().copy(id = id)

        runBlockingTest {
            assertThat(sut.saveObservation(observation)).isEqualTo(id)
        }
    }

    private fun createPersistenceObservation(): org.javafreedom.kcd.adapters.persistence.cassandra.model.Observation {
        return org.javafreedom.kcd.adapters.persistence.cassandra.model.Observation(
            "user",
            UUID.randomUUID(),
            "type",
            Instant.now(),
            Instant.now(),
            Instant.now(),
            "{\"element\":{\"type\":\"type\",\"comment\":\"element\",\"quantity\":{\"unit\":\"unit\",\"amount\":{\"type\":\"Int\",\"value\":100}},\"device\":\"device\",\"extension\":\"extension\"},\"component\":{\"type\":\"type\",\"comment\":\"component\",\"elements\":[{\"type\":\"type\",\"comment\":\"nestedElement1\",\"quantity\":{\"unit\":\"unit\",\"amount\":{\"type\":\"Double\",\"value\":100.0}},\"device\":\"device\",\"extension\":\"extension\"},{\"type\":\"type\",\"comment\":\"nestedElement2\",\"quantity\":{\"unit\":\"unit\",\"amount\":{\"type\":\"Int\",\"value\":10}},\"device\":\"device\",\"extension\":\"extension\"}]}}"
        )
    }

    private fun createObservationWithoutId(): Observation {
        return Observation(
            null, Instant.now(),
            EmbeddedAudit("user", Instant.now(), Instant.now()), true,
            Element(
                "type", null, Quantity("unit", 100),
                "device", "extension"
            ), null
        )
    }

}