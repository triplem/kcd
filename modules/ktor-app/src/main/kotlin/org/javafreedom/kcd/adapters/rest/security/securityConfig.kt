package org.javafreedom.kcd.adapters.rest.security

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import org.kodein.di.instance
import org.kodein.di.ktor.di

fun Application.securityConfig() {

    install(Authentication) {
        jwt {
            val jwtHandler by di().instance<JwtHandler>()
            verifier(jwtHandler.makeJwtVerifier())
            validate { credential ->
                JWTPrincipal(credential.payload)
            }
        }
    }

}
