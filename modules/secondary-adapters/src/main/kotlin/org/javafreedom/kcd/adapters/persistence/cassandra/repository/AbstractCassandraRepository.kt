package org.javafreedom.kcd.adapters.persistence.cassandra.repository

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.AsyncResultSet
import com.datastax.oss.driver.api.core.cql.BoundStatementBuilder
import kotlinx.coroutines.future.await
import java.nio.ByteBuffer

abstract class AbstractCassandraRepository(private val session: CqlSession) {

    companion object {
        // TODO make page size configurable in ui
        const val DEFAULT_PAGE_SIZE = 200
        const val SINGLE_PAGE_SIZE = 1
    }

    @Deprecated("this method uses paging, which is currently unused")
    suspend fun <T> find(
        statementBuilder: BoundStatementBuilder, page: ByteBuffer?,
        resultMapper: (AsyncResultSet) -> T
    ): T {
        statementBuilder.setPageSize(DEFAULT_PAGE_SIZE)
        page?.let { statementBuilder.setPagingState(it) }

        return session.executeAsync(statementBuilder.build())
            .thenApplyAsync { resultMapper(it) }
            .await()
    }

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
