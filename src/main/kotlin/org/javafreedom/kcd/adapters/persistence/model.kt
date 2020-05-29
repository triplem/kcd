package org.javafreedom.kcd.adapters.persistence

import java.nio.ByteBuffer
import java.time.Instant
import java.util.*

data class Observation(val id: UUID, val user: String, val type: String, val date: Instant, val createdAt: Instant,
                       val modifiedAt: Instant, val unit: String, val data: String)

data class ObservationList(val observations: List<Observation>, val page: ByteBuffer?)
