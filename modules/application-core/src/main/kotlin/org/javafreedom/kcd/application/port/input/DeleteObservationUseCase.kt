package org.javafreedom.kcd.application.port.input

import java.util.*

interface DeleteObservationUseCase {

    suspend fun deleteObservation(user: String, uuid: UUID): Boolean

}
