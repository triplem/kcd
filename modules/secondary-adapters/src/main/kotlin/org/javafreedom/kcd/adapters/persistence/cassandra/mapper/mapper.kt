package org.javafreedom.kcd.adapters.persistence.cassandra.mapper

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.javafreedom.kcd.adapters.persistence.DomainComponent
import org.javafreedom.kcd.adapters.persistence.DomainElement
import org.javafreedom.kcd.adapters.persistence.DomainEmbeddedAudit
import org.javafreedom.kcd.adapters.persistence.DomainObservation
import org.javafreedom.kcd.adapters.persistence.DomainQuantity
import org.javafreedom.kcd.adapters.persistence.cassandra.model.Component
import org.javafreedom.kcd.adapters.persistence.cassandra.model.DataHolder
import org.javafreedom.kcd.adapters.persistence.cassandra.model.Element
import org.javafreedom.kcd.adapters.persistence.cassandra.model.Observation
import org.javafreedom.kcd.adapters.persistence.cassandra.model.Quantity
import java.util.*
import java.util.stream.Collectors

fun DomainObservation.mapToPersistence(): Observation {
    val id = this.id ?: UUID.randomUUID()
    val type = this.element?.type ?: this.component?.type ?: error("type expected")

    val data: String = mapDataHolder(this.element, this.component)

    return Observation(
        this.audit.user, id, type, this.date, this.audit.createdAt,
        this.audit.modifiedAt, data
    )
}

fun mapDataHolder(element: DomainElement?, component: DomainComponent?): String {
    val elem = element?.mapToPersistence()
    val comp = component?.mapToPersistence()
    val dataHolder = DataHolder(elem, comp)

    val json = Json { }
    return json.encodeToString(dataHolder)
}

fun DomainElement.mapToPersistence() = Element(
    this.type,
    this.comment,
    this.quantity.mapToPersistence(),
    this.device,
    this.extension
)

fun DomainQuantity.mapToPersistence() = Quantity(
    this.unit,
    this.amount
)

fun DomainComponent.mapToPersistence() = Component(
    this.type,
    this.comment,
    this.elements.stream().map { it.mapToPersistence() }.collect(Collectors.toList())
)

fun Observation.mapToDomain(): DomainObservation {
    val dataHolder = deserializeDataHolder(this.data)
    val element = dataHolder.element?.mapToDomain()
    val component = dataHolder.component?.mapToDomain()
    val audit = DomainEmbeddedAudit(this.user, this.creationDate, this.modificationDate)

    return DomainObservation(
        this.id, this.dateOfObservation, audit,
        true, element, component
    )
}

fun deserializeDataHolder(dataHolderAsString: String): DataHolder {
    val json = Json {}
    return json.decodeFromString(dataHolderAsString)
}

fun Quantity.mapToDomain() = DomainQuantity(this.unit, this.amount)

fun Element.mapToDomain() = DomainElement(
    this.type, this.comment,
    this.quantity.mapToDomain(), this.device, this.extension
)

fun Component.mapToDomain() = DomainComponent(this.type,
    this.comment, this.elements.map { it.mapToDomain() })

class MappingException : RuntimeException()
