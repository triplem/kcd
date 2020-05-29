package org.javafreedom.kcd.adapters.persistence.repository

import com.datastax.oss.driver.api.core.CqlSession
import kotlinx.coroutines.future.await
import mu.KotlinLogging
import org.javafreedom.kcd.adapters.persistence.getStringNonEmpty

private val logger = KotlinLogging.logger {}

class UserTypeRepository(private val session: CqlSession): Repository(session) {

    suspend fun findUserTypes(user: String): List<String> {
        val statement = session.prepare("SELECT * FROM user_types WHERE user = ?")
        val statementBuilder = statement.boundStatementBuilder(user).setPageSize(DEFAULT_PAGE_SIZE)

        return find(statementBuilder, null) { it.currentPage().map { row -> row.getStringNonEmpty("type") } }
    }

    suspend fun insert(user: String, type: String) {
        val statement = session.prepare("INSERT INTO user_types (user, type) " +
                "VALUES (?, ?)")

        val statementBuilder = statement.boundStatementBuilder(user, type)

        session.executeAsync(statementBuilder.build()).await()
    }

}
