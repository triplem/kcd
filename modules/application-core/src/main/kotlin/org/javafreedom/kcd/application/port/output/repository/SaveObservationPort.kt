package org.javafreedom.kcd.application.port.output.repository

import org.javafreedom.kcd.domain.model.Observation
import java.util.*

/**
 * Provides methods to create Observations in the used repository.
 */
interface SaveObservationPort {

    /**
     * Create the given observation in the data store. Some information, which is always required
     * is given using explicit parameters and cannot be null.
     *
     * The returned UUID is the stored UUID, so if the given observation does contain an uuid
     * already, the returend uuid is the same, since we are just storing and not changing the
     * observation, regardless of the used data storeage. The UUID is a technical identifier of the
     * observation.
     *
     * @param observation the observation itself
     *
     * @return UUID the UUID of the stored observation
     */
    suspend fun saveObservation(observation: Observation): UUID

}
