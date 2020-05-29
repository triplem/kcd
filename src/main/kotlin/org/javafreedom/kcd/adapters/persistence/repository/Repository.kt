package org.javafreedom.kcd.adapters.persistence.repository

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.AsyncResultSet
import com.datastax.oss.driver.api.core.cql.BoundStatementBuilder
import kotlinx.coroutines.future.await
import java.nio.ByteBuffer

open class Repository(private val session: CqlSession) {

    companion object {
        // TODO make page size configurable in ui
        const val DEFAULT_PAGE_SIZE = 200
        const val SINGLE_PAGE_SIZE = 1
    }

    suspend fun <T> find(statementBuilder: BoundStatementBuilder, page: ByteBuffer?,
                     map: (AsyncResultSet) -> T): T {
        statementBuilder.setPageSize(DEFAULT_PAGE_SIZE)
        page?.let { statementBuilder.setPagingState(it) }

        return session.executeAsync(statementBuilder.build())
            .thenApplyAsync { map(it) }
            .await()
    }
}

class NoDataForQueryFound(message: String): RuntimeException(message)
