package org.javafreedom.kcd.application.port.input

import org.javafreedom.kcd.domain.model.Observation
import java.time.Instant
import java.util.*

interface LoadObservationUseCase {

    suspend fun loadObservation(user: String, uuid: UUID): Observation

    suspend fun loadObservations(
        user: String, from: Instant,
        to: Instant
    ): List<Observation>

    suspend fun loadObservationsByType(
        user: String, type: String, from: Instant,
        to: Instant
    ): List<Observation>

}
