package org.javafreedom.kcd.application.port.input

import org.javafreedom.kcd.domain.model.Observation
import java.util.*

/**
 * Observations are stored in the data store in this use-case.
 *
 * Note, that the observations have to be normalized before "entering" the
 * application level. This means, that adapters need to take care about the normalization
 * before the observation is entering the application level.
 *
 * The normalization process is described in the documentation for each type of
 * observation.
 */
interface SaveObservationUseCase {

    /**
     * Store the given observation in the data store
     *
     * @param observation the Observation to store
     *
     * @return the UUID of the stored Observation, which could be the same as the
     *         UUID of the given Observation if this attribute is filled
     */
    suspend fun saveObservation(observation: Observation): UUID

}
