package org.javafreedom.kcd.adapters.rest.security

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import org.javafreedom.kcd.common.subKodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import org.kodein.di.ktor.kodein

fun Application.securityConfig() {

    subKodein {
        bind<JwtHandler>() with singleton {
            JwtHandler(
                environment
            )
        }
    }

    install(Authentication) {
        jwt {
            val jwtHandler by kodein().instance<JwtHandler>()
            verifier(jwtHandler.makeJwtVerifier())
            validate { credential ->
                JWTPrincipal(credential.payload)
            }
        }
    }
}
