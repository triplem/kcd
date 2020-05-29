package org.javafreedom.kcd.adapters.persistence.cassandra.mapper

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

@Serializer(forClass = Number::class)
@OptIn(InternalSerializationApi::class)
object NumberSerializer : KSerializer<Number> {
    private const val KEY_TYPE = "type"
    private const val KEY_VALUE = "value"

    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("NumberSerializer", StructureKind.OBJECT)

    override fun serialize(encoder: Encoder, value: Number) {
        require(encoder is JsonEncoder)

        val jsonObject = buildJsonObject {
            put(KEY_TYPE, value::class.simpleName)
            put(KEY_VALUE, value)
        }

        encoder.encodeJsonElement(jsonObject)
    }

    override fun deserialize(decoder: Decoder): Number {
        val jsonInput = decoder as? JsonDecoder ?: error("Can only deserialize JSON")

        val jsonObject = jsonInput.decodeJsonElement().jsonObject

        val type = jsonObject.getValue(KEY_TYPE).jsonPrimitive.content
        val primitive = jsonObject.getValue(KEY_VALUE).jsonPrimitive

        return when (type) {
            Int::class.simpleName -> primitive.int
            Double::class.simpleName -> primitive.double
            else -> error("no valid type")
        }
    }
}
