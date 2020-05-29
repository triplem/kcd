@file:UseSerializers(ZonedDateTimeSerializer::class)
@file:OptIn(KtorExperimentalLocationsAPI::class)

package org.javafreedom.kcd.ktor

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.locations.*
import io.ktor.routing.*
import io.ktor.serialization.*
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import mu.KotlinLogging
import org.javafreedom.kcd.adapters.persistence.cassandra.cassandraModule
import org.javafreedom.kcd.adapters.rest.endpoint.IndexController
import org.javafreedom.kcd.adapters.rest.endpoint.ObservationController
import org.javafreedom.kcd.adapters.rest.restFeatures
import org.javafreedom.kcd.adapters.rest.security.JwtHandler
import org.javafreedom.kcd.adapters.rest.security.securityConfig
import org.javafreedom.kcd.adapters.rest.statusPagesFeature
import org.javafreedom.kcd.ktor.commons.UUIDSerializer
import org.javafreedom.kcd.ktor.commons.ZonedDateTimeSerializer
import org.kodein.di.AllInstances
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.ktor.controller.DIController
import org.kodein.di.ktor.di
import org.kodein.di.singleton
import org.kodein.type.erased

private val logger = KotlinLogging.logger {}

@Suppress("unused") // Referenced in application.conf
fun main(args: Array<String>): Unit = io.ktor.server.cio.EngineMain.main(args)

val baseDI = DI.Module("base") {
    bind<Json>() with singleton {
        Json {
            prettyPrint = true
            useArrayPolymorphism = false
            serializersModule = restSerializerModule
        }
    }
}

val controllerDI = DI.Module("controller") {
    bind<ObservationController>() with singleton {
        ObservationController(instance())
    }
    bind<IndexController>() with singleton {
        IndexController(instance())
    }
}

val restSerializerModule = SerializersModule {
    contextual(UUIDSerializer)
}

val appDI = DI.Module("application") {
    import(baseDI, allowOverride = false)
    import(controllerDI)

    import(cassandraModule)
    import(serviceModule)
}

fun Application.module(testing: Boolean = false) {
    module(testing, appDI)
}

fun Application.module(testing: Boolean = false, diCon: DI.Module) {
    logger.debug { "Starting main new" }

    di {
        import(diCon, allowOverride = false)

        bind<JwtHandler>() with singleton {
            JwtHandler(
                environment
            )
        }
    }

    if (isJwtEnabled()) {
        securityConfig()
    }

    restFeatures()
    statusPagesFeature()

    install(ContentNegotiation) {
        val jsonSupport by di().instance<Json>()
        json(jsonSupport)
    }

    install(Locations)

    routing {
        val controllerInstances by di().AllInstances(erased<DIController>())
        controllerInstances.forEach {
            it.apply {
                log.info("Installing '$this' routes.")
                installRoutes()
            }
        }
    }
}

fun Application.isJwtEnabled(): Boolean {
    return environment.config
        .propertyOrNull("jwt.enabled")?.getString()?.toBoolean() ?: false
}
