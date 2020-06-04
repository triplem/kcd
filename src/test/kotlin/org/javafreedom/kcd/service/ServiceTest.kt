package org.javafreedom.kcd.service

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.javafreedom.kcd.adapters.persistence.Observation
import org.javafreedom.kcd.adapters.persistence.ObservationList
import org.javafreedom.kcd.adapters.persistence.mapToDomain
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
import kotlin.test.assertNotNull

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

    suspend fun findByTime(user: String, from: Instant, to: Instant): List<org.javafreedom.kcd.domain.Observation> {
        val obsList = observationRepository.find(user, from, to, null)
        return listOf<org.javafreedom.kcd.domain.Observation>().apply { obsList.observations.map { it.mapToDomain() } }
    }

    @Test
    fun testFindByTime() {
        val from = Instant.MIN
        val to = Instant.MAX
        val json = Json(JsonConfiguration.Stable)

        val ob1 = Observation(UUID.randomUUID(), "user", "type1", Instant.now(), Instant.now(), Instant.now(),
            "unit", json.stringify(ArbitraryMapSerializer, mapOf("value" to "1")))
        val ob2 = Observation(UUID.randomUUID(), "user", "type2", Instant.now(), Instant.now(), Instant.now(),
            "unit", json.stringify(ArbitraryMapSerializer, mapOf("value" to "2")))
        val ob3 = Observation(UUID.randomUUID(), "user", "type3", Instant.now(), Instant.now(), Instant.now(),
            "unit", json.stringify(ArbitraryMapSerializer, mapOf("value" to "3")))

        coEvery { observationRepository.find("user", from, to, null) } returns
                ObservationList(listOf(ob1, ob2, ob3), null)

        runBlocking {
            val service = Service(userTypeRepository, observationRepository)
            val obs: List<org.javafreedom.kcd.domain.Observation> = service.findByTime("user", from, to)

            assertNotNull(obs)
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