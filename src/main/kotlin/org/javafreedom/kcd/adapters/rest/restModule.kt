@file:UseSerializers(ZonedDateTimeSerializer::class)
package org.javafreedom.kcd.adapters.rest

import com.datastax.oss.driver.api.core.CqlSession
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallId
import io.ktor.features.CallLogging
import io.ktor.features.ConditionalHeaders
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.callIdMdc
import io.ktor.http.HttpHeaders
import io.ktor.locations.Locations
import io.ktor.serialization.json
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.Json
import ktor_health_check.Health
import org.javafreedom.kcd.adapters.persistence.keyspaceExists
import org.javafreedom.kcd.common.ZonedDateTimeSerializer
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein
import java.util.UUID

fun Application.restModule() {

    install(ConditionalHeaders)

    install(DefaultHeaders) {
        header(HttpHeaders.Server, "KoCaDe")
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

    install(Locations)

    install(ContentNegotiation) {
        val jsonSupport by kodein().instance<Json>()
        json(jsonSupport)
    }

    install(Health) {
        readyCheck("cassandra") {
            val cqlSession by kodein().instance<CqlSession>()
            cqlSession.keyspaceExists()
        }
    }
}
