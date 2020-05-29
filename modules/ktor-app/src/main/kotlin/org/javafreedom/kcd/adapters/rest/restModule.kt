@file:UseSerializers(ZonedDateTimeSerializer::class)

package org.javafreedom.kcd.adapters.rest

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import kotlinx.serialization.UseSerializers
import ktor_health_check.Health
import org.javafreedom.kcd.application.common.HealthIndicator
import org.javafreedom.kcd.ktor.commons.ZonedDateTimeSerializer
import org.kodein.di.allInstances
import org.kodein.di.ktor.di
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

    install(Health) {
        val healthIndicators by di().allInstances<HealthIndicator>()
        healthIndicators.forEach {
            var readyPair = it.isReady()
            readyCheck(readyPair.first) {
                readyPair.second
            }
        }
    }
}
