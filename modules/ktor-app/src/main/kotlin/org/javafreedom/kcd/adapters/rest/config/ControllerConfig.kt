package org.javafreedom.kcd.adapters.rest.config

import io.ktor.application.Application
import io.ktor.routing.routing
import org.javafreedom.kcd.adapters.rest.endpoint.IndexController
import org.kodein.di.bind
import org.kodein.di.eagerSingleton
import org.kodein.di.instance
import org.kodein.di.ktor.subDI

fun Application.controllerConfig() {

    this.routing {
        subDI {
            bind(tag = "indexController") from eagerSingleton { IndexController(instance()) }
        }
    }

}
