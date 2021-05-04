package org.javafreedom.kcd.adapters.rest.security

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.auth0.jwt.JWT
import io.ktor.application.*
import io.ktor.config.*
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class JwtHandlerTest {

    @MockK
    lateinit var applicationEnvironment: ApplicationEnvironment

    @Test
    fun testCreateToken() {
        every { applicationEnvironment.config.property("jwt.domain") } returns TestStringConfigValue("domain")
        every { applicationEnvironment.config.property("jwt.audience") } returns TestStringConfigValue("audience")
        every { applicationEnvironment.config.property("jwt.secret") } returns TestStringConfigValue("secret")

        val jwtHandler = JwtHandler(applicationEnvironment)
        val result = jwtHandler.createToken("userName")

        assertThat(result).isNotNull()

        assertThat(JWT.decode(result).subject).isEqualTo("userName")

        val verifier = jwtHandler.makeJwtVerifier()
        val verifcationResult = verifier.verify(result)

        assertThat(verifcationResult.subject).isEqualTo("userName")
    }





}

class TestStringConfigValue(val testValue: String) : ApplicationConfigValue {
    override fun getList(): List<String> {
        TODO("Not yet implemented")
    }

    override fun getString(): String {
        return testValue
    }

}