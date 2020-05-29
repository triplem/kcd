package org.javafreedom.kcd.common

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.getContextualOrDefault
import mu.KotlinLogging

/**
 * This handles serialization and deserialization of arbitrary JSON trees represented as
 * `Map<String, Any?>`
 *
 * Serialization involves [ImplicitReflectionSerializer] so this will probably
 * not work in a multi-platform environment.
 *
 * Using `@Serializable` objects in the tree respects their declared serializers.
 *
 * This class is stolen from
 * https://github.com/uport-project/kotlin-did-jwt/blob/master/jwt/src/main/java/me/uport/sdk/jwt/model/ArbitraryMapSerializer.kt
 */
object ArbitraryMapSerializer : KSerializer<Map<String, Any?>> {

    ////////////////////////
    // converting to Json //
    ////////////////////////
    @ImplicitReflectionSerializer
    private fun Map<*, *>.toJsonObject(): JsonObject = JsonObject(map {
        it.key.toString() to it.value.toJsonElement()
    }.toMap())

    @ImplicitReflectionSerializer
    private fun Any?.toJsonElement(): JsonElement = when (this) {
        null -> JsonNull
        is Number -> JsonPrimitive(this)
        is String -> JsonPrimitive(this)
        is Boolean -> JsonPrimitive(this)
        is Map<*, *> -> this.toJsonObject()
        is Iterable<*> -> JsonArray(this.map { it.toJsonElement() })
        is Array<*> -> JsonArray(this.map { it.toJsonElement() })
        else -> {
            //supporting classes that declare serializers
            val jsonParser = Json(JsonConfiguration.Stable)
            val serializer = jsonParser.context.getContextualOrDefault(this)
            jsonParser.toJson(serializer, this)
        }
    }

    //////////////////////////////////////////////////
    // converting back to primitives (no type info) //
    //////////////////////////////////////////////////
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

    override fun deserialize(decoder: Decoder): Map<String, Any?> {
        val asJsonObject: JsonObject = decoder.decodeSerializableValue(JsonObjectSerializer)
        return asJsonObject.toPrimitiveMap()
    }

    override val descriptor: SerialDescriptor
        get() = SerialDescriptor("arbitrary map", PolymorphicKind.OPEN)

    @ImplicitReflectionSerializer
    override fun serialize(encoder: Encoder, value: Map<String, Any?>) {
        val asJsonObj: JsonObject = value.toJsonObject()
        encoder.encode(JsonObjectSerializer, asJsonObj)
    }
}