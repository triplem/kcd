package org.javafreedom.kcd.adapters.persistence.cassandra

import com.github.f4b6a3.uuid.UuidCreator
import org.javafreedom.kcd.adapters.persistence.cassandra.mapper.mapToDomain
import org.javafreedom.kcd.adapters.persistence.cassandra.mapper.mapToPersistence
import org.javafreedom.kcd.adapters.persistence.cassandra.repository.ObservationRepository
import org.javafreedom.kcd.application.common.HealthIndicator
import org.javafreedom.kcd.application.port.output.repository.DeleteObservationPort
import org.javafreedom.kcd.application.port.output.repository.FindObservationPort
import org.javafreedom.kcd.application.port.output.repository.SaveObservationPort
import org.javafreedom.kcd.domain.model.Observation
import java.time.Instant
import java.util.*

class CassandraObservationAdapter(private val repo: ObservationRepository) : SaveObservationPort,
    DeleteObservationPort, FindObservationPort, HealthIndicator {

    override suspend fun deleteObservation(user: String, uuid: UUID): Boolean {
        repo.delete(user, uuid)
        return true
    }

    override suspend fun findObservation(user: String, uuid: UUID): Observation {
        return repo.find(user, uuid).mapToDomain()
    }

    override suspend fun findObservationsBetween(
        user: String,
        from: Instant,
        to: Instant
    ): List<Observation> {
        return repo.find(user, from, to).observations.map { it.mapToDomain() }
    }

    override suspend fun findObservationsByTypeAndBetween(
        user: String,
        type: String,
        from: Instant,
        to: Instant
    ): List<Observation> {
        return repo.find(user, type, from, to).observations.map { it.mapToDomain() }
    }

    override suspend fun saveObservation(observation: Observation): UUID {
        val id: UUID = observation.id ?: UuidCreator
            .getTimeBased(observation.date, null, null)

        val copied = observation.copy(id = id)
        repo.upsert(copied.mapToPersistence())

        return  id
    }

    override fun isReady(): Pair<String, Boolean> {
        TODO("Not yet implemented")
    }

}
