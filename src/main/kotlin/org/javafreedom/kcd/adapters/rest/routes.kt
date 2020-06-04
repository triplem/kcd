@file:OptIn(KtorExperimentalLocationsAPI::class)
package org.javafreedom.kcd.adapters.rest

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.auth.authentication
import io.ktor.locations.*
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.routing
import mu.KotlinLogging
import org.javafreedom.kcd.adapters.persistence.mapToPersistence
import java.util.*

private val logger = KotlinLogging.logger {}

@SuppressWarnings("detekt:EmptyDefaultConstructor")
@Location("/")
class Index()

fun Route.index() {

    get<Index> {
        call.respondText("HELLO WORLD!", contentType = io.ktor.http.ContentType.Text.Plain)
    }

}

@SuppressWarnings("detekt:EmptyDefaultConstructor")
@Location("/observation")
class ObservationRoute()

fun Route.observation() {

    post<ObservationRoute> {
        val principal = call.authentication.principal

        logger.debug { "call.authentication: ${call.authentication}"}
        logger.debug { "principal: $principal" }

        val receivedObservation = call.receive<RequestObservation>()
        logger.debug { "receiveDataPoint: $receivedObservation" }

        val domainObservation = receivedObservation.value.mapToDomain("user", UUID.randomUUID())
        logger.debug { "domain: $domainObservation" }

        // service stuff happening here...
        val repositoryModel = domainObservation.mapToPersistence(UUID.randomUUID())
        logger.debug("repo: {}", repositoryModel)

        // transform the domain datapoint to the repository datapoint (should be done in the adapter really)
//            repo.find(id)?.let {
//                call.respond(HttpStatusCode.OK, it)
//            } ?: call.respondText(status = HttpStatusCode.NotFound) { "There is no record with id: $id" }
    }


}

fun Application.routes() {
    logger.debug { "Starting module" }

    logger.debug { "application: ${this}" }

    routing {
        index()
        observation()

//        authenticate {
//            val repo by kodein().instance<Repository>()
//
//            get("/") {
//                val principal = call.authentication.principal
//                log.debug("principal: {}", principal)
//                log.debug("repo: {}", repo)
//
//
//            }
//
//        }
    }
}

//suspend fun ApplicationCall.respondRedirect(location: Any)
//        = respondRedirect(url = url(location), permanent = false)
