package org.javafreedom.kcd.ktor.commons

import io.ktor.application.*

class KtorHelper(val environment: ApplicationEnvironment) {

    fun isProduction(): Boolean {
        return when (environment.config.propertyOrNull("ktor.environment")?.getString()) {
            "prod" -> true
            else -> false
        }
    }
}
