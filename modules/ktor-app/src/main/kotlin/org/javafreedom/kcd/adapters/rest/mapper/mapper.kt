package org.javafreedom.kcd.adapters.rest.mapper

import io.ktor.util.*
import org.javafreedom.kcd.adapters.rest.DomainComponent
import org.javafreedom.kcd.adapters.rest.DomainElement
import org.javafreedom.kcd.adapters.rest.DomainEmbeddedAudit
import org.javafreedom.kcd.adapters.rest.DomainObservation
import org.javafreedom.kcd.adapters.rest.DomainQuantity
import org.javafreedom.kcd.adapters.rest.model.Component
import org.javafreedom.kcd.adapters.rest.model.Element
import org.javafreedom.kcd.adapters.rest.model.Observation
import org.javafreedom.kcd.adapters.rest.model.Quantity
import java.time.Instant
import java.time.ZoneId
import java.util.*

fun Observation.mapToDomain(user: String, uuid: UUID?): DomainObservation {

    this.dateOfObservation.zone

    var element: DomainElement? = null
    var component: DomainComponent? = null
    if (this.component.elements.size == 1) {
        val elem = this.component.elements.first()

        element = elem.mapToDomain()
    } else {
        component = this.component.mapToDomain()
    }

    val id = this.id ?: uuid ?: UUID.randomUUID()

    return DomainObservation(
        id, dateOfObservation.toInstant(),
        DomainEmbeddedAudit(user, Instant.now(), Instant.now()), true,
        element, component
    )

}

fun Element.mapToDomain() = DomainElement(
    this.type,
    this.comment,
    this.quantity.mapToDomain(),
    this.device,
    this.extension
)

fun Component.mapToDomain() = DomainComponent(
    this.type,
    this.comment,
    this.elements.map { it.mapToDomain() }
)

fun Quantity.mapToDomain() = DomainQuantity(this.unit, this.amount)


fun DomainObservation.mapToRest(): Observation {
    val type = this.element?.type ?: this.component?.type ?: "undefined"

    val element = this.element
    val component = if (element != null) {
        Component(element.type, element.comment, listOf(element.mapToRest()))
    } else {
        val component = this.component
        if (component != null) {
            Component(component.type, component.comment, component.elements.map { it.mapToRest() })
        } else {
            throw DataConversionException("element or component should be filled")
        }
    }

    return Observation(
        this.id, this.date.atZone(ZoneId.systemDefault()),
        type, component
    )
}

fun DomainElement.mapToRest(): Element {
    return Element(this.type, this.comment, this.quantity.mapToRest(), this.device, this.extension)
}

fun DomainQuantity.mapToRest() = Quantity(this.unit, this.amount)
