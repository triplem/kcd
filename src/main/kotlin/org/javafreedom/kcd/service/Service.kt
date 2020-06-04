package org.javafreedom.kcd.service

import org.javafreedom.kcd.adapters.persistence.mapToDomain
import org.javafreedom.kcd.adapters.persistence.mapToPersistence
import org.javafreedom.kcd.adapters.persistence.repository.ObservationRepository
import org.javafreedom.kcd.adapters.persistence.repository.UserTypeRepository
import org.javafreedom.kcd.core.UserNotAllowedException
import org.javafreedom.kcd.domain.Observation
import java.time.Instant
import java.util.*

class Service(val userTypeRepository: UserTypeRepository,
              val observationRepository: ObservationRepository) {

    suspend fun upsert(observation: Observation): Observation {
        val id = observation.id?.let { observation.id } ?: UUID.randomUUID()

        recalculateValue(observation)

        val persistenceModel = observation.mapToPersistence(id)
        observationRepository.insert(persistenceModel)
        userTypeRepository.insert(observation.user, observation.type)

        return persistenceModel.mapToDomain()
    }

    suspend fun findByTime(user: String, from: Instant, to: Instant): List<Observation> {
        val obsList = observationRepository.find(user, from, to, null)
        return listOf<Observation>().apply { obsList.observations.map { it.mapToDomain() } }
    }

    suspend fun findById(user: String, id: UUID): Observation {
        val persistenceModel = observationRepository.find(id)

        val model = persistenceModel.mapToDomain()

        if (model.user != user) {
            throw UserNotAllowedException()
        }

        return model
    }

    private fun recalculateValue(observation: Observation) {
        val newValue = ValueRecalculations.recalculations[observation.details.unit]?.forEach {
            it(observation.details.data["value"])
        }

        val origValue = observation.details.data["data"]

        if (newValue != origValue) {
            val data = observation.details.data as MutableMap
            data["orig-value"] = origValue
            data["value"] = newValue
        }
    }
}
