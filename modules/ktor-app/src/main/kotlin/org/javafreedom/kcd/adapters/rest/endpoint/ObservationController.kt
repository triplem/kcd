package org.javafreedom.kcd.adapters.rest.endpoint

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import mu.KotlinLogging
import org.javafreedom.kcd.adapters.rest.mapper.mapToDomain
import org.javafreedom.kcd.adapters.rest.mapper.mapToRest
import org.javafreedom.kcd.adapters.rest.model.RequestObservation
import org.javafreedom.kcd.application.port.input.LoadObservationUseCase
import org.javafreedom.kcd.application.port.input.SaveObservationUseCase
import org.kodein.di.instance
import org.kodein.di.ktor.controller.AbstractDIController
import java.util.*

private val logger = KotlinLogging.logger {}

@Serializable
data class IdResponse(val id: String)

class ObservationController(application: Application) : AbstractDIController(application) {

    private val saveObservationUseCase: SaveObservationUseCase by instance()
    private val loadObservationUseCase: LoadObservationUseCase by instance()

    override fun Route.getRoutes() {
        post<ObservationIndex> {
            val principal = call.authentication.principal

            logger.debug { "call.authentication: ${call.authentication}" }
            logger.debug { "principal: $principal" }

            val receivedObservation = call.receive<RequestObservation>()

            logger.debug { "receiveDataPoint: $receivedObservation" }

            val domainObservation = receivedObservation.value.mapToDomain("user", UUID.randomUUID())
            logger.debug { "domain: $domainObservation" }

            val uuid = saveObservationUseCase.saveObservation(domainObservation)
            call.respond(HttpStatusCode.Created, IdResponse(uuid.toString()))
        }

        get<ObservationIndex.ById> { param ->
            val principal = call.authentication.principal

            logger.debug { "call.authentication: ${call.authentication}" }
            logger.debug { "principal: $principal" }

            try {
                logger.debug { "id: '${param.observationId}'" }

                val domainObservation = loadObservationUseCase
                    .loadObservation("user", UUID.fromString(param.observationId))
                val requestObservation = domainObservation.mapToRest()

                call.respond(HttpStatusCode.OK, requestObservation)

            } catch (cause: SerializationException) {
                throw IllegalArgumentException(cause.message)
            }

        }
    }
}
