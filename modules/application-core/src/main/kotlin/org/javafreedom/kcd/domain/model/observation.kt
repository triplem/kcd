package org.javafreedom.kcd.domain.model

import java.time.Instant
import java.util.*

/**
 * This class represents when the dataset was created and/or modified in this application by whom.
 *
 * It does not represent date of the observation outside of this application (eg issued).
 */
data class EmbeddedAudit(val user: String, val createdAt: Instant, val modifiedAt: Instant)

data class Quantity(val unit: String, val amount: Number)

/**
 * a concrete value of the measurement
 */
data class Element(
    val type: String,
    val comment: String? = null,
    val quantity: Quantity,
    val device: String,
    val extension: String
)

/**
 * a component is a holder for multiple measurements of the same type
 * eg. for a blood pressure measurement, which contains usually two datapoints (systolic, diastolic)
 */
data class Component(
    val type: String,
    val comment: String?,
    val elements: Collection<Element>
)

/**
 * represents a measurement taken on the date
 */
data class Observation(
    val id: UUID? = null, val date: Instant, val audit: EmbeddedAudit,
    val valid: Boolean = true, val element: Element?,
    val component: Component?
)
