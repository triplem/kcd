package org.javafreedom.kcd.application.port.output.repository

import org.javafreedom.kcd.domain.model.Observation
import java.time.Instant
import java.util.*

/**
 * Provides methods to find Observations in the used repository.
 *
 * An Observation is always assigned to a single user, and therefor all methods contain the
 * user as a required parameter.
 */
interface FindObservationPort {

    /**
     * retrieves the observation using the given UUID
     *
     * @param user the id of the user, to which the observation is assigned
     * @param uuid the uuid of the observation to delete
     *
     * @throws ObservationNotFoundException if Observation is not found using the given user
     */
    suspend fun findObservation(user: String, uuid: UUID): Observation

    /**
     * retrieves all observations between the two given dates (inclusive, to reflect the sql/cql
     * BETWEEN keyword).
     *
     * @param user the id of the user, to which the observation is assigned
     * @param from the date to search from (inclusive)
     * @param to the date to search to (inclusive)
     *
     * @throws ObservationNotFoundException if Observation is not found using the given user
     */
    suspend fun findObservationsBetween(user: String, from: Instant, to: Instant): List<Observation>

    /**
     * retrieves all observations between the two given dates (inclusive, to reflect the sql/cql
     * BETWEEN keyword) having the given type.
     *
     * The type is just a plain string, so any type stored in the db can be retrieved. There are
     * no checks on this string.
     *
     * @param user the id of the user, to which the observation is assigned
     * @param type the type of the observation
     * @param from the date to search from (inclusive)
     * @param to the date to search to (inclusive)
     *
     * @throws ObservationNotFoundException if Observation is not found using the given user
     */
    suspend fun findObservationsByTypeAndBetween(
        user: String, type: String, observationTimeFrom: Instant,
        observationTimeTo: Instant
    ): List<Observation>

}
