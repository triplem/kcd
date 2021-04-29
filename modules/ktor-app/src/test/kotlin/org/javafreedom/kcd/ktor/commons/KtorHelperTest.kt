package org.javafreedom.kcd.ktor.commons

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import io.ktor.application.*
import io.ktor.config.*
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class KtorHelperTest {

    @MockK
    lateinit var environment: ApplicationEnvironment

    @MockK
    lateinit var config: ApplicationConfig

    @MockK
    lateinit var value: ApplicationConfigValue

    @Test
    fun isProduction_true() {
        every { value.getString() } returns "prod"
        every { config.propertyOrNull("ktor.environment") } returns value
        every { environment.config } returns config

        val sut = KtorHelper(environment)
        assertThat(sut.isProduction()).isTrue()
    }

    @Test
    fun isProduction_false() {
        every { value.getString() } returns "test"
        every { config.propertyOrNull("ktor.environment") } returns value
        every { environment.config } returns config

        val sut = KtorHelper(environment)
        assertThat(sut.isProduction()).isFalse()
    }

    @Test
    fun isProduction_falseOnNull() {
        every { config.propertyOrNull("ktor.environment") } returns null
        every { environment.config } returns config

        val sut = KtorHelper(environment)
        assertThat(sut.isProduction()).isFalse()
    }

}