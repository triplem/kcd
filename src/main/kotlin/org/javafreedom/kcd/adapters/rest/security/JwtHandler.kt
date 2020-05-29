package org.javafreedom.kcd.adapters.rest.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.ApplicationEnvironment
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class JwtHandler(val environment: ApplicationEnvironment) {
    val jwtIssuer = environment.config.property("jwt.domain").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()
    val algorithm = Algorithm.HMAC256(jwtSecret)

    fun createToken(userName: String): String {
        val issuedAt = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())

        return JWT.create()
            .withIssuer(jwtIssuer)
            .withAudience(jwtAudience)
            .withIssuedAt(issuedAt)
            .withSubject(userName)
            .sign(algorithm)
    }

    fun makeJwtVerifier(): JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(jwtIssuer)
        .withAudience(jwtAudience)
        .build()

}
