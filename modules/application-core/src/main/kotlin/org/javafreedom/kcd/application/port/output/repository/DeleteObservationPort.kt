package org.javafreedom.kcd.application.port.output.repository

import java.util.*

/**
 * Provides the methods for the Deletion of an observation in the repository.
 */
interface DeleteObservationPort {

    /**
     * Delete the observation with the given UUID if and only if the given user is assigned
     * to this observation.
     * If the user is not assigned to the UUID, then nothing will be deleted.
     *
     * @param user the id of the user, to which the observation is assigned
     * @param uuid the uuid of the observation to delete
     *
     * @return true, if the observation was deleted successfully, false otherwise
     */
    suspend fun deleteObservation(user: String, uuid: UUID): Boolean

}
