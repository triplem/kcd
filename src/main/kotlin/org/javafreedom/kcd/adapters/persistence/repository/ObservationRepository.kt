package org.javafreedom.kcd.adapters.persistence.repository

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.AsyncResultSet
import com.datastax.oss.driver.api.core.cql.BoundStatementBuilder
import com.datastax.oss.driver.api.core.cql.Row
import kotlinx.coroutines.future.await
import mu.KotlinLogging
import org.javafreedom.kcd.adapters.persistence.*
import java.nio.ByteBuffer
import java.time.Instant
import java.util.*

private val logger = KotlinLogging.logger {}

class ObservationRepository(private val session: CqlSession): Repository(session) {

    suspend fun find(id: UUID): Observation {
        val statement = session.prepare("SELECT * FROM observation WHERE id = ?")
        val statementBuilder = statement.boundStatementBuilder(id).setPageSize(SINGLE_PAGE_SIZE)

        return session.executeAsync(statementBuilder.build())
            .thenApplyAsync { ars ->
                ars.one()?.let {
                    rowToEvent(it)
                } ?: throw NoDataForQueryFound("id = $id")
            }
            .await()
    }

    suspend fun find(user: String, page: ByteBuffer?): ObservationList {
        val statement = session.prepare("SELECT * FROM observation_by_date WHERE user = ?")
        val statementBuilder = statement.boundStatementBuilder(user).setPageSize(DEFAULT_PAGE_SIZE)

        return find(statementBuilder, page)
    }

    suspend fun find(user: String, from: Instant, to: Instant, page: ByteBuffer?): ObservationList {
        val statement = session.prepare("SELECT * FROM observation_by_date WHERE user = ? " +
                "AND date > ? AND date < ?")
        val statementBuilder = statement.boundStatementBuilder(user, from, to)

        return find(statementBuilder, page)
    }

    suspend fun find(user: String, type: String, page: ByteBuffer?): ObservationList {
        val statement = session.prepare("SELECT * FROM observation_by_type WHERE user = ? " +
                "AND type = ?")
        val statementBuilder = statement.boundStatementBuilder(user, type)

        return find(statementBuilder, page)
    }

    suspend fun find(user: String, type: String, from: Instant, to: Instant, page: ByteBuffer?): ObservationList {
        val statement = session.prepare("SELECT * FROM observation WHERE user = ? " +
                "AND type = ? AND date > ? AND date < ?")
        val statementBuilder = statement.boundStatementBuilder(user, type, from, to)

        return find(statementBuilder, page)
    }

    suspend fun find(statementBuilder: BoundStatementBuilder, page: ByteBuffer?): ObservationList {
        return find(statementBuilder, page) { map(it) }
    }

    suspend fun delete(id: UUID) {
        val statement = session.prepare("DELETE FROM observation WHERE id = ?")
        val statementBuilder = statement.boundStatementBuilder(id)

        session.executeAsync(statementBuilder.build()).await()
    }

    suspend fun insert(observation: Observation) {
        val statement = session.prepare("INSERT INTO observation (id, user, type, " +
                "date, createdAt, modifiedAt, unit, data) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")

        val statementBuilder = statement.boundStatementBuilder(observation.id, observation.user,
            observation.type, observation.date, observation.createdAt, observation.modifiedAt,
            observation.unit, observation.data)

        session.executeAsync(statementBuilder.build()).await()
    }

    private fun map(ars: AsyncResultSet): ObservationList {
        val list = ars.currentPage().map { rowToEvent(it) }

        return ObservationList(
            list,
            ars.executionInfo.pagingState
        )
    }

    private fun rowToEvent(row: Row) = Observation(
        row.getUuidNonEmpty("id"),
        row.getStringNonEmpty("user"),
        row.getStringNonEmpty("type"),
        row.getInstantNonEmpty("date"),
        row.getInstantNonEmpty("createdAt"),
        row.getInstantNonEmpty("modifiedAt"),
        row.getStringNonEmpty("unit"),
        row.getStringNonEmpty("data")
    )

}
