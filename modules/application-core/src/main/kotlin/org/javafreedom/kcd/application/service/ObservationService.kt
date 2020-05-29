package org.javafreedom.kcd.application.service

import org.javafreedom.kcd.application.port.input.DeleteObservationUseCase
import org.javafreedom.kcd.application.port.input.LoadObservationUseCase
import org.javafreedom.kcd.application.port.input.SaveObservationUseCase
import org.javafreedom.kcd.application.port.output.repository.DeleteObservationPort
import org.javafreedom.kcd.application.port.output.repository.FindObservationPort
import org.javafreedom.kcd.application.port.output.repository.SaveObservationPort
import org.javafreedom.kcd.domain.model.Observation
import java.time.Instant
import java.util.*

class ObservationService(
    private val deleteObservationPort: DeleteObservationPort,
    private val findObservationPort: FindObservationPort,
    private val saveObservationPort: SaveObservationPort,
    private val validationOperation: ValidationOperation
) : DeleteObservationUseCase,
    LoadObservationUseCase, SaveObservationUseCase {

    override suspend fun deleteObservation(user: String, uuid: UUID): Boolean {
        return this.deleteObservationPort.deleteObservation(user, uuid)
    }

    override suspend fun loadObservation(user: String, uuid: UUID): Observation {
        return this.findObservationPort.findObservation(user, uuid)
    }

    override suspend fun loadObservations(user: String, from: Instant, to: Instant): List<Observation> {
        return this.findObservationPort.findObservationsBetween(user, from, to)
    }

    override suspend fun loadObservationsByType(
        user: String,
        type: String,
        from: Instant,
        to: Instant
    ): List<Observation> {
        return this.findObservationPort.findObservationsByTypeAndBetween(user, type, from, to)
    }

    override suspend fun saveObservation(observation: Observation): UUID {


        return this.saveObservationPort.saveObservation(observation)
    }
}
