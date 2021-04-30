package org.javafreedom.kcd.adapters.persistence.cassandra.mapper

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

@Serializable
data class TestValue(
    val name: String,
    @Serializable(with = NumberSerializer::class) val value: Number
)

internal class NumberSerializerTest {

    @Test
    fun deserialize_Int() {
        val testValue = TestValue("Int", 4711)

        val json = Json { }
        val encoded = json.encodeToString(testValue)
        assertThat(encoded).contains("{\"type\":\"Int\",\"value\":4711}")

        val decoded = json.decodeFromString<TestValue>(encoded)
        assertThat(decoded.value)
            .isInstanceOf(Int::class)
            .isEqualTo(4711)
    }

    @Test
    fun deserialize_Double() {
        val testValue = TestValue("Double", 4711.0)

        val json = Json { }
        val encoded = json.encodeToString(testValue)
        assertThat(encoded).contains("{\"type\":\"Double\",\"value\":4711.0}")

        val decoded = json.decodeFromString<TestValue>(encoded)
        assertThat(decoded.value)
            .isInstanceOf(Double::class)
            .isEqualTo(4711.0)
    }

    @Test
    fun deserialize_Long() {
        val json = Json { }
        val encoded = "{\"name\":\"Long\",\"value\":{\"type\":\"Long\",\"value\":4711}}"

        assertThat {
            json.decodeFromString<TestValue>(encoded)
        }.isFailure().hasMessage("no valid type")
    }

    @Test
    fun deserialize_Short() {
        val json = Json { }
        val encoded = "{\"name\":\"Short\",\"value\":{\"type\":\"Short\",\"value\":4711}}"

        assertThat {
            json.decodeFromString<TestValue>(encoded)
        }.isFailure().hasMessage("no valid type")
    }

    @Test
    fun deserialize_Float() {
        val json = Json { }
        val encoded = "{\"name\":\"Float\",\"value\":{\"type\":\"Float\",\"value\":4711.0}}"

        assertThat {
            json.decodeFromString<TestValue>(encoded)
        }.isFailure().hasMessage("no valid type")
    }

}