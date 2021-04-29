package org.javafreedom.kcd.adapters.persistence.cassandra.repository

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isSuccess
import assertk.assertions.isTrue
import com.datastax.oss.driver.api.core.servererrors.InvalidQueryException
import com.github.f4b6a3.uuid.UuidCreator
import kotlinx.coroutines.runBlocking
import org.javafreedom.kcd.adapters.persistence.cassandra.model.Observation
import org.javafreedom.kcd.adapters.persistence.cassandra.model.ObservationList
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.BeforeTest
import kotlin.test.Test

class ObservationRepositoryTest : RepositoryTest<ObservationRepository>() {

    companion object {
        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            RepositoryTest.beforeAll()

            prepareDatabase("datapoint.cql")
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            RepositoryTest.afterAll()
        }
    }

    @BeforeTest
    fun createRepo() {
        createRepository<ObservationRepository>()
    }

    @Test
    fun `find by non-existent id`() {
        val wrongUuid = UuidCreator.getTimeBasedWithRandom()
        assertThat {
            runBlocking {
                getSut().find("wrong", wrongUuid)
            }
        }.isFailure().hasMessage("user = 'wrong' and id = '$wrongUuid'")
    }

    @Test
    fun `find with empty user`() {
        val wrongUuid = UuidCreator.getTimeBasedWithRandom()
        assertThat {
            runBlocking {
                getSut().find("", wrongUuid)
            }
        }.isFailure()
            .isInstanceOf(InvalidQueryException::class)
            .hasMessage("Key may not be empty")
    }

    @Test
    fun `insert Data`() {
        val entryDate = Instant.now().truncatedTo(ChronoUnit.MILLIS)
        val id = UuidCreator.getTimeBasedWithRandom(Instant.now(), null)

        val observation = Observation(
            "user", id, "type", entryDate, entryDate,
            entryDate, "data"
        )

        runBlocking {
            assertThat {
                getSut().upsert(observation)

                val dpsById = getSut().find("user", id)
                assertThat(observation).isEqualTo(dpsById)

                val from = entryDate.minus(1, ChronoUnit.MINUTES)
                val to = entryDate.plus(1, ChronoUnit.MINUTES)
                val dpsByUserAndTimeframe = getSut().find("user", from, to)
                dpsByUserAndTimeframe.observations
            }.isSuccess().contains(observation)

            getSut().delete("user", id)
        }
    }

    @Test
    fun `find existing by user, type and between`() {
        val entryDate = Instant.now().truncatedTo(ChronoUnit.MILLIS)
        val id = UuidCreator.getTimeBasedWithRandom(entryDate, null)

        val observation = Observation(
            "user", id, "type", entryDate, entryDate,
            entryDate, "data"
        )

        val expected = ObservationList(listOf(observation), null)

        runBlocking {
            assertThat {
                getSut().upsert(observation)

                getSut().find(
                    "user", "type",
                    entryDate.minusSeconds(1200),
                    entryDate.plusSeconds(1200)
                )
            }.isSuccess().isEqualTo(expected)

            getSut().delete("user", id)
        }
    }

    @Test
    fun `find existing by user and between`() {
        val entryDate = Instant.now().truncatedTo(ChronoUnit.MILLIS)
        val id = UuidCreator.getTimeBasedWithRandom(entryDate, null)

        val observation = Observation(
            "user", id, "type", entryDate, entryDate,
            entryDate, "data"
        )

        val expected = ObservationList(listOf(observation), null)

        runBlocking {
            assertThat {
                getSut().upsert(observation)

                getSut().find(
                    "user",
                    entryDate.minusSeconds(1200),
                    entryDate.plusSeconds(1200)
                )
            }.isSuccess().isEqualTo(expected)

            getSut().delete("user", id)
        }
    }

    @Test
    fun `delete existing data`() {
        val entryDate = Instant.now().truncatedTo(ChronoUnit.MILLIS)
        val id = UuidCreator.getTimeBasedWithRandom(Instant.now(), null)

        val observation = Observation(
            "user", id, "type", entryDate, entryDate,
            entryDate, "data"
        )

        runBlocking {
            assertThat {
                getSut().upsert(observation)
                getSut().delete("user", id)
            }.isSuccess().isTrue()
        }
    }

    @Test
    fun `delete not existing data`() {
        val id = UuidCreator.getTimeBasedWithRandom(Instant.now(), null)

        runBlocking {
            assertThat {
                getSut().delete("user", id)
            }.isSuccess().isFalse()
        }
    }

}