@file:UseSerializers(ZonedDateTimeSerializer::class)
package org.javafreedom.kcd

import io.ktor.config.MapApplicationConfig
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.*
import mu.KotlinLogging
import org.javafreedom.kcd.adapters.rest.Observation
import org.javafreedom.kcd.adapters.rest.RequestObservation
import org.javafreedom.kcd.common.ZonedDateTimeSerializer
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertEquals

private val logger = KotlinLogging.logger {}

class ApplicationTest {

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
            main()
        }) {
            with(handleRequest(HttpMethod.Get, "/")) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("HELLO WORLD!", response.content)
            }

            val test= handleRequest(HttpMethod.Post, "/") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                val date = ZonedDateTime.now(ZoneId.of("Europe/Berlin"))
                val string = date.format(DateTimeFormatter.ISO_INSTANT)
                logger.debug("test1: {}", string)

                val json = Json(JsonConfiguration(useArrayPolymorphism = true))

                val bs = JsonObject(mapOf("value" to JsonPrimitive(6.7)))
                val bsValue = Observation(
                    null, "bloodglucose", "mmol/l", ZonedDateTime.now(),
                    bs
                )
                val test1 = RequestObservation(bsValue)
                logger.debug("bs: {}", json.stringify(RequestObservation.serializer(), test1))

//https://stackoverflow.com/questions/55417112/kotlinx-serializer-create-a-quick-json-to-send
                val element = test1.value.data as JsonObject

                if (test1.value.unit == "mmol/l") {
                    val data = test1.value.data
                    val map = data.jsonObject.toMutableMap()

                    val newValue: Int = if (map.containsKey("value")) {
                            map["value"]?.doubleOrNull?.let {
                                map["value"]?.doubleOrNull?.times(18.02)?.roundToInt()
                            } ?: -1
                        } else {
                            -1
                        }

                    map.put("value", JsonPrimitive(newValue))

                    val recalculated = test1.value.copy(unit = "mg/dl", data = JsonObject(map))

                    logger.warn { "YES ${recalculated}" }

                    logger.debug { "unequal" }

                    val jsonString = recalculated.data.toString()
                    logger.debug { "string: $jsonString" }

                    val jsonAgain = json.parseJson(jsonString)
                    logger.debug { "jsonAgain: $jsonAgain" }
                }


                val bpList = JsonArray(listOf(
                    JsonObject(mapOf(
                        "sub-type" to JsonPrimitive("systolic"),
                        "value" to JsonPrimitive(70))),
                    JsonObject(mapOf(
                        "sub-type" to JsonPrimitive("diastolic"),
                        "value" to JsonPrimitive(150)))
                ))
                var bp = JsonObject(mapOf("value" to bpList))
                val bpValue = Observation(
                    null, "bloodpressure", "mmHg", ZonedDateTime.now(),
                    bp
                )
                val test = RequestObservation(bpValue)
                logger.debug("bp: {}", json.stringify(RequestObservation.serializer(), test))

                val element1 = test.value.data as JsonObject
                val map1 = element1.toPrimitiveMap()

                logger.warn { "map1: $map1" }

                setBody(json.stringify(RequestObservation.serializer(), test))
            }
        }
    }

    // stolen from https://github.com/uport-project/kotlin-did-jwt/blob/master/jwt/src/main/java/me/uport/sdk/jwt/model/ArbitraryMapSerializer.kt
    private fun JsonObject.toPrimitiveMap(): Map<String, Any?> =
        this.content.map {
            it.key to it.value.toPrimitive()
        }.toMap()

    private fun JsonElement.toPrimitive(): Any? = when (this) {
        is JsonNull -> null
        is JsonObject -> this.toPrimitiveMap()
        is JsonArray -> this.map { it.toPrimitive() }
        is JsonLiteral -> {
            if (isString) {
                contentOrNull
            } else {
                booleanOrNull ?: longOrNull ?: doubleOrNull
            }
        }
        else -> null
    }

    @Test
    fun testJsonDateSerialization() {
        val date = ZonedDateTime.now(ZoneId.of("Europe/Berlin"))
        val string = date.format(DateTimeFormatter.ISO_INSTANT)
        logger.debug("testDateIsoInstant: {}", string)

        val json = Json(JsonConfiguration(useArrayPolymorphism = true))

        val testObject = SerializerTest(date)
        val serialized = json.stringify(SerializerTest.serializer(), testObject)
        logger.debug("serialized: {}", serialized)

        val deserialized = json.parse(SerializerTest.serializer(), serialized)
        logger.debug("deserialized: {}", deserialized)

        val jsonString = "{\"date\":\"2020-05-06T19:18:58.330+01:30\"}"
        val deserializedString = json.parse(SerializerTest.serializer(), jsonString)
        logger.debug("deserializedString: {}", deserializedString)
        val serializedString = json.stringify(SerializerTest.serializer(), deserializedString)
        logger.debug("serializedString: {}", serializedString)
    }
}

@Serializable
data class SerializerTest(val date: ZonedDateTime)

