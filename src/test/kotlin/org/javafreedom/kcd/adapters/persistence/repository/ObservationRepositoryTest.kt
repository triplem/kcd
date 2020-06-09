package org.javafreedom.kcd.adapters.persistence.repository

import com.datastax.oss.driver.api.core.NoNodeAvailableException
import kotlinx.coroutines.runBlocking
import org.javafreedom.kcd.adapters.persistence.Observation
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.assertTrue
import kotlin.test.junit5.JUnit5Asserter.assertEquals

class ObservationRepositoryTest: RepositoryTest<ObservationRepository>() {

    companion object {
        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            prepareDatabase("datapoint.cql")
        }
    }

//    @BeforeTest
    fun createRepo() {
        createRepository<ObservationRepository>()
    }

//    @Test
    fun `find by non-existent id`() {
        val wrongUuid = UUID.randomUUID()
        val exception = assertThrows<NoDataForQueryFound> {
            runBlocking {
                getSut().find(wrongUuid)
            }
        }

        assertEquals("exception expected", "id = $wrongUuid", exception.message)
    }

//    @Test
    fun `insert Data`() {
        val entryDate = Instant.now().truncatedTo(ChronoUnit.MILLIS)
        val id = UUID.randomUUID();

        val observation = Observation(
            id, "user", "type", entryDate, entryDate,
            entryDate, "unit", "data"
        )

        runBlocking {
            try {
                getSut().insert(observation)

                val dpsById = getSut().find(id)
                assertEquals("events", observation, dpsById)

                val from = entryDate.minus(1, ChronoUnit.MINUTES)
                val to = entryDate.plus(1, ChronoUnit.MINUTES)
                val dpsByUserAndTimeframe = getSut().find("user", from, to, null)
                assertEquals("events", observation, dpsByUserAndTimeframe.observations.get(0))

            } catch (e: NoNodeAvailableException) {
                assertTrue(false)
            }
        }

        val exception = assertThrows<NoDataForQueryFound> {
            runBlocking {
                getSut().delete(id)
                getSut().find(id)
            }
        }
        assertEquals("exception expected", "id = $id", exception.message)
    }

}