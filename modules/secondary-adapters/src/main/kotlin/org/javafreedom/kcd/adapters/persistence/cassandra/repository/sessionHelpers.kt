package org.javafreedom.kcd.adapters.persistence.cassandra.repository

import com.datastax.oss.driver.api.core.CqlSession

fun CqlSession.keyspaceExists(): Boolean {
    return this.keyspace.isPresent
            && this.metadata.getKeyspace(this.keyspace.get()).map { true }.orElse(false)
}

fun cassandraSession(): CqlSession {
    return CqlSession.builder().withKeyspace("kcd").build()
}
