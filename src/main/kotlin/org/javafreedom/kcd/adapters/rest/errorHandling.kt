@file:UseSerializers(ZonedDateTimeSerializer::class)
package org.javafreedom.kcd.adapters.rest

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentTransformationException
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import kotlinx.serialization.UseSerializers
import mu.KotlinLogging
import org.javafreedom.kcd.common.ZonedDateTimeSerializer

private val logger = KotlinLogging.logger {}

fun Application.errorHandling() {

    install(StatusPages) {
        exception<AuthenticationException> { e ->
            logger.error(e) { "error" }
            val failure = e.message?.also { Failure(e.message) } ?: Failure(
                ""
            )
            call.respond(HttpStatusCode.Unauthorized, failure)
        }
        exception<AuthorizationException> { _ ->
            call.respond(HttpStatusCode.Forbidden)
        }
        exception<IllegalArgumentException> { _ ->
            call.respond(HttpStatusCode.BadRequest)
        }
        exception<ContentTransformationException> { e ->
            logger.error(e) { "error" }
            call.respond(HttpStatusCode.BadRequest)
        }
    }
}
