package org.javafreedom.kcd.application.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.javafreedom.kcd.application.port.output.repository.DeleteObservationPort
import org.javafreedom.kcd.application.port.output.repository.FindObservationPort
import org.javafreedom.kcd.application.port.output.repository.SaveObservationPort
import org.javafreedom.kcd.domain.model.Observation

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Instant
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockKExtension::class)
internal class ObservationServiceTest {

    @MockK
    lateinit var deleteObservationPort: DeleteObservationPort

    @MockK
    lateinit var findObservationPort: FindObservationPort

    @MockK
    lateinit var saveObservationPort: SaveObservationPort

    @Test
    fun deleteObservation() {
        val observationService = ObservationService(
            deleteObservationPort, findObservationPort,
            saveObservationPort
        )

        coEvery { deleteObservationPort.deleteObservation(any(), any()) } returns true

        runBlockingTest {
            assertThat(observationService.deleteObservation("user", UUID.randomUUID())).isTrue()
        }
    }

    @Test
    fun loadObservation() {
        val observationService = ObservationService(
            deleteObservationPort, findObservationPort,
            saveObservationPort
        )

        val observation = mockk<Observation>()

        coEvery { findObservationPort.findObservation(any(), any()) } returns observation

        runBlockingTest {
            assertThat(observationService.loadObservation("user", UUID.randomUUID()))
                .isEqualTo(observation)
        }
    }

    @Test
    fun loadObservations() {
        val observationService = ObservationService(
            deleteObservationPort, findObservationPort,
            saveObservationPort
        )

        val observation = mockk<Observation>()

        coEvery { findObservationPort.findObservationsBetween(any(), any(), any()) } returns listOf(
            observation
        )

        runBlockingTest {
            assertThat(
                observationService.loadObservations(
                    "user",
                    Instant.now().minusSeconds(120),
                    Instant.now().plusSeconds(120)
                )
            )
                .isEqualTo(listOf(observation))
        }
    }

    @Test
    fun loadObservationsByType() {
        val observationService = ObservationService(
            deleteObservationPort, findObservationPort,
            saveObservationPort
        )

        val observation = mockk<Observation>()

        coEvery {
            findObservationPort.findObservationsByTypeAndBetween(
                any(), any(),
                any(), any()
            )
        } returns listOf(observation)

        runBlockingTest {
            assertThat(
                observationService.loadObservationsByType(
                    "user", "type",
                    Instant.now().minusSeconds(120),
                    Instant.now().plusSeconds(120)
                )
            )
                .isEqualTo(listOf(observation))
        }
    }

    @Test
    fun saveObservation() {
        val observationService = ObservationService(
            deleteObservationPort, findObservationPort,
            saveObservationPort
        )

        val observation = mockk<Observation>()
        val id = UUID.randomUUID()

        coEvery { saveObservationPort.saveObservation(observation) } returns id

        runBlockingTest {
            assertThat(observationService.saveObservation(observation)).isEqualTo(id)
        }
    }
}