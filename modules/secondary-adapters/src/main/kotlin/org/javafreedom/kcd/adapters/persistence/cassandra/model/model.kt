package org.javafreedom.kcd.adapters.persistence.cassandra.model

import kotlinx.serialization.Serializable
import org.javafreedom.kcd.adapters.persistence.cassandra.mapper.NumberSerializer
import java.nio.ByteBuffer
import java.time.Instant
import java.util.*

/**
 * represents an observation in the datasstore
 */
data class Observation(
    val user: String, val id: UUID, val type: String,
    val dateOfObservation: Instant, val creationDate: Instant,
    val modificationDate: Instant, val data: String
)

/**
 * dataholder could be versioned to reflect changes in the domainmodel, which are not
 * necessary for db finder
 */
@Serializable
data class DataHolder(val element: Element?, val component: Component?)

@Serializable
data class Element(
    val type: String,
    val comment: String? = null,
    val quantity: Quantity,
    val device: String,
    val extension: String
)

@Serializable
data class Quantity(
    val unit: String,
    @Serializable(with = NumberSerializer::class) val amount: Number
)

@Serializable
data class Component(
    val type: String,
    val comment: String? = null,
    val elements: Collection<Element>
)

data class ObservationList(val observations: List<Observation>, val pagingState: ByteBuffer?)
