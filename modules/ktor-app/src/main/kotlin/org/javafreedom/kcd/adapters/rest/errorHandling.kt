@file:UseSerializers(ZonedDateTimeSerializer::class)
package org.javafreedom.kcd.adapters.rest

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import kotlinx.serialization.UseSerializers
import mu.KotlinLogging
import org.javafreedom.kcd.adapters.rest.model.Failure
import org.javafreedom.kcd.adapters.rest.model.HttpException
import org.javafreedom.kcd.adapters.rest.model.MappingException
import org.javafreedom.kcd.ktor.commons.KtorHelper
import org.javafreedom.kcd.ktor.commons.ZonedDateTimeSerializer

private val logger = KotlinLogging.logger {}

fun Application.statusPagesFeature() {
    val ktorHelper = KtorHelper(environment)
    val isProduction = ktorHelper.isProduction()

    install(StatusPages) {
        exception<IllegalArgumentException> { e ->
            logger.error(e) { "error" }
            call.respond(HttpStatusCode.BadRequest, constructFailure(isProduction, context, e))
        }
        exception<MappingException> { e ->
            logger.error(e) { "error" }
            call.respond(HttpStatusCode.BadRequest)
        }
        exception<HttpException> { e ->
            logger.error(e) { e.description }
            call.respond(e.code)
        }
    }
}

private fun constructFailure(
    isProduction: Boolean, context: ApplicationCall,
    t: Throwable
): Failure {
    val code: Int? = if (t is HttpException) t.code.value else null
    val stacktrace: String? =
        if (debugHeader(context) && !isProduction) t.stackTraceToString() else null

    return Failure(code, t.message.orEmpty(), stacktrace)
}

internal fun debugHeader(context: ApplicationCall): Boolean {
    return context.request.header("X-Debug") != null
}
