package org.javafreedom.kcd.adapters.persistence.cassandra.repository

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.AsyncResultSet
import com.datastax.oss.driver.api.core.cql.BoundStatementBuilder
import kotlinx.coroutines.future.await

abstract class AbstractCassandraRepository(private val session: CqlSession) {

    suspend fun <T> find(
        statementBuilder: BoundStatementBuilder,
        resultMapper: (AsyncResultSet) -> T
    ): T {
        return session.executeAsync(statementBuilder.build())
            .thenApplyAsync { resultMapper(it) }
            .await()
    }

}

class NoDataForQueryFound(message: String) : RuntimeException(message)
