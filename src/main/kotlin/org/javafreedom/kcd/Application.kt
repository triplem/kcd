@file:UseSerializers(ZonedDateTimeSerializer::class)
@file:OptIn(KtorExperimentalLocationsAPI::class)
package org.javafreedom.kcd

import io.ktor.application.Application
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.serialization.DefaultJsonConfiguration
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import mu.KotlinLogging
import org.javafreedom.kcd.adapters.rest.restModule
import org.javafreedom.kcd.adapters.persistence.persistenceModule
import org.javafreedom.kcd.adapters.rest.deprecated.serializerModule
import org.javafreedom.kcd.adapters.rest.errorHandling
import org.javafreedom.kcd.adapters.rest.routes
import org.javafreedom.kcd.adapters.rest.security.securityConfig
import org.javafreedom.kcd.common.ArbitraryMapSerializer
import org.javafreedom.kcd.common.ZonedDateTimeSerializer
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import org.kodein.di.ktor.kodein

private val logger = KotlinLogging.logger {}

@Suppress("unused") // Referenced in application.conf
fun Application.main() {
    logger.debug { "Starting main" }

    kodein {
        bind<Json>() with singleton { Json(DefaultJsonConfiguration.copy(
            prettyPrint = true,
            useArrayPolymorphism = false
            ),
            SerializersModule { ArbitraryMapSerializer })
        }
    }

    securityConfig()
    errorHandling()
    restModule()
    persistenceModule()
    routes()
}
