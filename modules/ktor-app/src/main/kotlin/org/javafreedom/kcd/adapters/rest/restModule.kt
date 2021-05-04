@file:UseSerializers(ZonedDateTimeSerializer::class)

package org.javafreedom.kcd.adapters.rest

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import kotlinx.serialization.UseSerializers
import org.javafreedom.kcd.ktor.commons.ZonedDateTimeSerializer
import java.util.*

fun Application.restFeatures() {

    install(ConditionalHeaders)

    install(DefaultHeaders) {
        header(HttpHeaders.Server, "KCD")
    }

    install(CallId) {
        generate {
            UUID.randomUUID().toString()
        }
        header(HttpHeaders.XCorrelationId)
    }

    install(CallLogging) {
        callIdMdc(HttpHeaders.XCorrelationId)
    }

}
