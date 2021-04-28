package org.javafreedom.kcd.adapters.persistence.cassandra.repository

import com.datastax.oss.driver.api.core.cql.Row
import java.time.Instant
import java.util.*

fun Row.getUuidNonEmpty(name: String): UUID {
    return this.getUuid(name) ?: throw InvalidIdException()
}

fun Row.getStringNonEmpty(name: String): String {
    return this.getString(name).orEmpty()
}

fun Row.getInstantNonEmpty(name: String): Instant {
    return this.getInstant(name) ?: Instant.MIN
}

class InvalidIdException : RuntimeException()
