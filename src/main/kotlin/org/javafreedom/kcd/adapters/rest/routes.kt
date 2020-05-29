@file:OptIn(KtorExperimentalLocationsAPI::class)
package org.javafreedom.kcd.adapters.rest

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.auth.authentication
import io.ktor.locations.*
import io.ktor.request.receive
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.routing
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.json.JsonDecodingException
import mu.KotlinLogging
import org.javafreedom.kcd.adapters.persistence.mapToPersistence
import org.javafreedom.kcd.adapters.rest.security.JwtHandler
import org.javafreedom.kcd.service.LoginRegister
import org.javafreedom.kcd.service.User
import org.javafreedom.kcd.service.users
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein
import java.util.*

private val logger = KotlinLogging.logger {}

@Location("/")
class Index()

fun Route.index() {

    get<Index> {
        call.respondText("HELLO WORLD!", contentType = io.ktor.http.ContentType.Text.Plain)
    }

}

@Location("/login")
class Login()

fun Route.login() {

    post<Login> {
        val jwtHandler by kodein().instance<JwtHandler>()

        var name: String
        try {
            val post = call.receiveOrNull<LoginRegister>() ?: throw AuthenticationException(
                "No Credentials given."
            )

            val user = users.getOrPut(post.user) {
                User(
                    post.user,
                    post.password
                )
            }

            if (user.password != post.password) throw AuthenticationException(
                "Invalid Credentials given."
            )

            name = user.name

        } catch (e: JsonDecodingException) {
            throw AuthenticationException("No valid Credentials given.")
        } catch (e: MissingFieldException) {
            throw AuthenticationException("No valid Credentials given.")
        }
        call.respond(mapOf("token" to jwtHandler.createToken(name)))
    }
}

@Location("/datapoint")
class DataPointRoute()

fun Route.datapoint() {
    post<DataPointRoute> {
        val principal = call.authentication.principal

        logger.debug { "call.authentication: ${call.authentication}"}
        logger.debug { "principal: $principal" }

        val receivedDataPoint = call.receive<RequestDataPoint>()
        logger.debug { "receiveDataPoint: $receivedDataPoint" }

        val domainDataPoint = receivedDataPoint.value.mapToDomain("user", UUID.randomUUID())
        logger.debug { "domain: $domainDataPoint" }

        // service stuff happening here...
        val repositoryModel = domainDataPoint.mapToPersistence(UUID.randomUUID())
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
        login()
        datapoint()

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
