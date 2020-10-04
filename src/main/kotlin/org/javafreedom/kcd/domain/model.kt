package org.javafreedom.kcd.domain

import java.time.Instant
import java.util.*

import org.javafreedom.kcd.common.UUIDSerializer

data class EmbeddedAudit(val createdAt: Instant, val modifiedAt: Instant)

data class Observation(val id: UUID?, val user: String, val type: String, val audit: EmbeddedAudit,
                       val details: ObservationDetails)

data class ObservationDetails(val unit: String, val date: Instant, val data: Map<String, Any?>)
