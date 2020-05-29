package org.javafreedom.kcd.adapters.persistence.memory

import org.javafreedom.kcd.adapters.persistence.cassandra.repository.NoDataForQueryFound
import org.javafreedom.kcd.application.common.HealthIndicator
import org.javafreedom.kcd.application.port.output.repository.DeleteObservationPort
import org.javafreedom.kcd.application.port.output.repository.FindObservationPort
import org.javafreedom.kcd.application.port.output.repository.SaveObservationPort
import org.javafreedom.kcd.domain.model.Observation
import java.time.Instant
import java.util.*

class MemoryObservationAdapter : SaveObservationPort, DeleteObservationPort, FindObservationPort,
    HealthIndicator {

    val dataHolder: MutableMap<String, MutableSet<Triple<UUID, String, Observation>>> =
        mutableMapOf()

    override suspend fun saveObservation(observation: Observation): UUID {
        val user = observation.audit.user
        val id = observation.id ?: UUID.randomUUID()
        val type = observation.element?.type ?: observation.component?.type
                                                ?: error("not allowed - null type")

        val copied = observation.copy(id = id)

        if (!dataHolder.containsKey(user)) {
            val set = mutableSetOf(Triple(id, type, copied))
            dataHolder[user] = set
        } else {
            deleteObservation(user, id)
            val triple = Triple(id, type, copied)
            val set = dataHolder[user] ?: mutableSetOf()
            set.add(triple)
        }

        return id
    }

    override suspend fun deleteObservation(user: String, uuid: UUID): Boolean {
        return if (dataHolder.containsKey(user)) {
            dataHolder[user]!!.removeIf { it.first == uuid }
        } else {
            false
        }
    }

    override suspend fun findObservation(user: String, uuid: UUID): Observation {
        return if (dataHolder.containsKey(user)) {
            val set = dataHolder[user] ?: throw NoDataForQueryFound("user $user not found in repo")

            set.asSequence()
                .filter { it.first == uuid }
                .map { it.third }
                .firstOrNull() ?: throw NoDataForQueryFound("id = $uuid and user = $user")
        } else {
            throw NoDataForQueryFound("id = $uuid and user = $user")
        }
    }

    override suspend fun findObservationsBetween(
        user: String,
        from: Instant,
        to: Instant
    ): List<Observation> {
        return if (dataHolder.containsKey(user)) {
            val set = dataHolder[user] ?: throw NoDataForQueryFound("user $user not found in repo")

            set.asSequence()
                .map { it.third }
                .filter { it.date.isAfter(from) && it.date.isBefore(to) }
                .ifEmpty { throw NoDataForQueryFound("user = $user and between") }
                .toList()
        } else {
            throw NoDataForQueryFound("user = $user")
        }
    }

    override suspend fun findObservationsByTypeAndBetween(
        user: String,
        type: String,
        observationTimeFrom: Instant,
        observationTimeTo: Instant
    ): List<Observation> {

        return findInDataHolder(user) {
            asSequence()
                .filter { it.second == type }
                .map { it.third }
                .filter { it.date.isAfter(observationTimeFrom) && it.date.isBefore(observationTimeTo) }
                .ifEmpty { throw NoDataForQueryFound("user = $user, type = $type, datefrom = ...") }
                .toList()
        }

    }

    private fun findInDataHolder(
        user: String,
        block: MutableSet<Triple<UUID, String, Observation>>.() -> List<Observation>
    ): List<Observation> {

        return if (dataHolder.containsKey(user)) {
            val set = dataHolder[user] ?: throw NoDataForQueryFound("user $user not found in repo")
            set.run(block)
        } else {
            throw NoDataForQueryFound("user = $user")
        }
    }

    override fun isReady(): Pair<String, Boolean> {
        return Pair(this.javaClass.simpleName, true)
    }

}
