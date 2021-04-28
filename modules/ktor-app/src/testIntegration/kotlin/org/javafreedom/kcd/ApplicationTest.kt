@file:UseSerializers(ZonedDateTimeSerializer::class)

package org.javafreedom.kcd

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import io.ktor.config.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.javafreedom.kcd.adapters.persistence.memory.memoryModule
import org.javafreedom.kcd.adapters.rest.model.Component
import org.javafreedom.kcd.adapters.rest.model.Element
import org.javafreedom.kcd.adapters.rest.model.Observation
import org.javafreedom.kcd.adapters.rest.model.Quantity
import org.javafreedom.kcd.adapters.rest.model.RequestObservation
import org.javafreedom.kcd.ktor.baseDI
import org.javafreedom.kcd.ktor.commons.ZonedDateTimeSerializer
import org.javafreedom.kcd.ktor.controllerDI
import org.javafreedom.kcd.ktor.module
import org.javafreedom.kcd.ktor.serviceModule
import org.kodein.di.DI
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.test.Test
import kotlin.test.assertEquals

private val logger = KotlinLogging.logger {}

val testDI = DI.Module("test-application") {
    import(baseDI, allowOverride = false)
    import(controllerDI)

    import(memoryModule)
    import(serviceModule)
}

class ApplicationTest {

    //https://stackoverflow.com/questions/55417112/kotlinx-serializer-create-a-quick-json-to-send
    @Test
    fun testRoot() {
        withTestApplication({
            (environment.config as MapApplicationConfig).apply {
                // Set here the properties
                put("jwt.domain", "03e156f6058a13813816065")
                put("jwt.audience", "test")
                put("jwt.realm", "test")
                put("jwt.secret", "test")
            }
            module(true, testDI)
        }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("HELLO WORLD!", response.content)
            }
        }
    }

    @Test
    fun testObservation_NOk_wrongJson() {
        withTestApplication({
            (environment.config as MapApplicationConfig).apply {
                // Set here the properties
                put("jwt.domain", "03e156f6058a13813816065")
                put("jwt.audience", "test")
                put("jwt.realm", "test")
                put("jwt.secret", "test")
            }
            module(true, testDI)
        }) {
            handleRequest(HttpMethod.Post, "/observation") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody("\"wrong\":10000")
            }.apply {
                assertThat(response.status()).isEqualTo(HttpStatusCode.BadRequest)
                assertThat(response.content).isNotNull()
                    .contains("Unexpected JSON token at offset 0: Expected")
            }
        }
    }

    @Test
    fun testObservation_Ok() {
        withTestApplication({
            (environment.config as MapApplicationConfig).apply {
                // Set here the properties
                put("jwt.domain", "03e156f6058a13813816065")
                put("jwt.audience", "test")
                put("jwt.realm", "test")
                put("jwt.secret", "test")
            }
            module(true, testDI)
        }) {
            handleRequest(HttpMethod.Post, "/observation") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                val date = ZonedDateTime.now(ZoneId.of("Europe/Berlin"))
                val string = date.format(DateTimeFormatter.ISO_INSTANT)
                logger.debug("test1: {}", string)

                val json = Json {
                    prettyPrint = true
                    useArrayPolymorphism = false
//                    serializersModule = restSerializerModule
                }

                setBody(json.encodeToString(RequestObservation.serializer(), generateBsRestData()))
            }.apply {
                assertThat(response.status()).isEqualTo(HttpStatusCode.Created)
            }
        }
    }

    private fun generateBsRestData(): RequestObservation {
        val element = Element(
            "BloodSugar", "comment", Quantity("mg/dl", 99),
            "device", "extension"
        )

        val bsValue = Observation(
            null, ZonedDateTime.now(),
            "BloodSugar", Component("BloodSugar", "comment", listOf(element))
        )
        return RequestObservation(bsValue)
    }

    @Test
    fun testJsonDateSerialization() {
        val date = ZonedDateTime.now(ZoneId.of("Europe/Berlin"))
        val string = date.format(DateTimeFormatter.ISO_INSTANT)
        logger.debug("testDateIsoInstant: {}", string)

        val json = Json {
            useArrayPolymorphism = true
        }

        val testObject = SerializerTest(date)
        val serialized = json.encodeToString(SerializerTest.serializer(), testObject)
        logger.debug("serialized: {}", serialized)

        val deserialized = json.parseToJsonElement(serialized)
        logger.debug("deserialized: {}", deserialized)

        val jsonString = "{\"date\":\"2020-05-06T19:18:58.330+01:30\"}"
        val deserializedString = json.parseToJsonElement(jsonString)
        logger.debug("deserializedString: {}", deserializedString)
        val serializedString = deserializedString.toString()
        logger.debug("serializedString: {}", serializedString)
    }
}

@Serializable
data class SerializerTest(val date: ZonedDateTime)
