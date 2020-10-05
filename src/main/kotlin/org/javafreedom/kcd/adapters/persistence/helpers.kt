package org.javafreedom.kcd.adapters.persistence

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.Row
import java.time.Instant
import java.util.UUID

fun CqlSession.keyspaceExists(): Boolean {
    return this.keyspace.isPresent
            && this.metadata.getKeyspace(this.keyspace.get()).map { true }.orElse(false)
}

fun Row.getUuidNonEmpty(name: String): UUID {
    return this.getUuid(name)?.also { it } ?: throw InvalidIdException()
}

fun Row.getStringNonEmpty(name: String): String {
    return this.getString(name).orEmpty()
}

fun Row.getInstantNonEmpty(name: String): Instant {
    return this.getInstant(name)?.let { it } ?: Instant.MIN
}

fun cassandraSession(): CqlSession {
    return CqlSession.builder().withKeyspace("kcd").build()
}

class InvalidIdException: RuntimeException()