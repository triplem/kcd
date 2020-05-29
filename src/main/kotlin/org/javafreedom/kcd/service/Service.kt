package org.javafreedom.kcd.service

import org.javafreedom.kcd.adapters.persistence.mapToDomain
import org.javafreedom.kcd.adapters.persistence.mapToPersistence
import org.javafreedom.kcd.adapters.persistence.repository.ObservationRepository
import org.javafreedom.kcd.adapters.persistence.repository.UserTypeRepository
import org.javafreedom.kcd.core.UserNotAllowedException
import org.javafreedom.kcd.domain.Observation
import java.util.*

class Service(val userTypeRepository: UserTypeRepository,
              val observationRepository: ObservationRepository) {

    suspend fun upsert(observation: Observation): UUID {
        val id = observation.id?.let { observation.id } ?: UUID.randomUUID()

        val persistenceModel = observation.mapToPersistence(id)
        observationRepository.insert(persistenceModel)
        userTypeRepository.insert(observation.user, observation.type)

        return id
    }

    suspend fun findById(user: String, id: UUID): Observation {
        val persistenceModel = observationRepository.find(id)

        val model = persistenceModel.mapToDomain()

        if (model.user != user) {
            throw UserNotAllowedException()
        }

        return model
    }
}
