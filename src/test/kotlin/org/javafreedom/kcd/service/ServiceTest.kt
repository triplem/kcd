package org.javafreedom.kcd.service

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.javafreedom.kcd.adapters.persistence.Observation
import org.javafreedom.kcd.adapters.persistence.repository.ObservationRepository
import org.javafreedom.kcd.adapters.persistence.repository.UserTypeRepository
import org.javafreedom.kcd.common.ArbitraryMapSerializer
import org.javafreedom.kcd.core.UserNotAllowedException
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ServiceTest {

    @Test
    fun testToQuarter() {
        val dateTime1 = LocalDateTime.of(2019, 1, 3, 12, 3)

        val dateTime2 = LocalDateTime.of(2019, 3, 3, 23, 59)

        val dateTime3 = LocalDateTime.of(1975, 12, 3, 23, 59)
    }

    val observationRepository = mockk<ObservationRepository>()
    val userTypeRepository = mockk<UserTypeRepository>()

    @Test
    fun testFindById() {
        val uuid = UUID.randomUUID()
        val json = Json(JsonConfiguration.Stable)
        val data = mapOf("key" to "value")

        coEvery { observationRepository.find(uuid) } returns Observation(uuid, "user", "type",
            Instant.now(), Instant.now(), Instant.now(), "unit", json.stringify(ArbitraryMapSerializer, data))

        runBlocking {
            val service = Service(userTypeRepository, observationRepository)
            val obs: org.javafreedom.kcd.domain.Observation = service.findById("user", uuid)

            assertEquals(uuid, obs.id)
        }

    }

    @Test
    fun testFindByIdWrongUser() {
        val uuid = UUID.randomUUID()
        val json = Json(JsonConfiguration.Stable)
        val data = mapOf("key" to "value")

        coEvery { observationRepository.find(uuid) } returns Observation(uuid, "user", "type",
            Instant.now(), Instant.now(), Instant.now(), "unit", json.stringify(ArbitraryMapSerializer, data))

        runBlocking {
            val service = Service(userTypeRepository, observationRepository)
            assertFailsWith<UserNotAllowedException>("Test") {
                service.findById("test-user", uuid)

            }
        }
    }

}