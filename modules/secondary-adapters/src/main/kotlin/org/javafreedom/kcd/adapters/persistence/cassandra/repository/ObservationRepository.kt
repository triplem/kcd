package org.javafreedom.kcd.adapters.persistence.cassandra.repository

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.AsyncResultSet
import com.datastax.oss.driver.api.core.cql.BoundStatementBuilder
import com.datastax.oss.driver.api.core.cql.Row
import com.datastax.oss.driver.api.core.uuid.Uuids
import kotlinx.coroutines.future.await
import org.javafreedom.kcd.adapters.persistence.cassandra.model.Observation
import org.javafreedom.kcd.adapters.persistence.cassandra.model.ObservationList
import java.nio.ByteBuffer
import java.time.Instant
import java.util.*

class ObservationRepository(private val session: CqlSession) :
    AbstractCassandraRepository(session) {

    suspend fun find(user: String, id: UUID): Observation {
        val statement = session.prepare("SELECT * FROM observation WHERE user = ? AND id = ?")
        val statementBuilder =
            statement.boundStatementBuilder(user, id).setPageSize(SINGLE_PAGE_SIZE)

        return session.executeAsync(statementBuilder.build())
            .thenApplyAsync { ars ->
                ars.one()?.let {
                    rowToEvent(it)
                } ?: throw NoDataForQueryFound("user = '$user' and id = '$id'")
            }
            .await()
    }

    suspend fun find(user: String, from: Instant, to: Instant, page: ByteBuffer?): ObservationList {
        val statement = session.prepare(
            "SELECT * FROM observation WHERE user = ? " +
                    "AND id > ? AND id < ?"
        )

        val fromUuid = Uuids.startOf(from.toEpochMilli())
        val toUuid = Uuids.endOf(to.toEpochMilli())

        val statementBuilder = statement.boundStatementBuilder(user, fromUuid, toUuid)

        return find(statementBuilder, page)
    }

    suspend fun find(
        user: String,
        type: String,
        from: Instant,
        to: Instant,
        page: ByteBuffer?
    ): ObservationList {
        val statement = session.prepare(
            "SELECT * FROM observation_by_type WHERE user = ? " +
                    "AND type = ? AND id > ? AND id < ?"
        )

        val fromUuid = Uuids.startOf(from.toEpochMilli())
        val toUuid = Uuids.endOf(to.toEpochMilli())

        val statementBuilder = statement.boundStatementBuilder(user, type, fromUuid, toUuid)

        return find(statementBuilder, page)
    }

    suspend fun find(statementBuilder: BoundStatementBuilder, page: ByteBuffer?): ObservationList {
        return find(statementBuilder) { map(it) }
    }

    suspend fun delete(user: String, id: UUID) {
        val statement = session.prepare("DELETE FROM observation WHERE user = ? AND id = ?")
        val statementBuilder = statement.boundStatementBuilder(user, id)

        session.executeAsync(statementBuilder.build()).await()
    }

    suspend fun upsert(observation: Observation) {
        val statement = session.prepare(
            "INSERT INTO observation (user, id, type, " +
                    "date, createdAt, modifiedAt, data) VALUES (?, ?, ?, ?, ?, ?, ?)"
        )

        val statementBuilder = statement.boundStatementBuilder(
            observation.user, observation.id,
            observation.type, observation.dateOfObservation, observation.creationDate,
            observation.modificationDate, observation.data
        )

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
        row.getStringNonEmpty("user"),
        row.getUuidNonEmpty("id"),
        row.getStringNonEmpty("type"),
        row.getInstantNonEmpty("date"),
        row.getInstantNonEmpty("createdAt"),
        row.getInstantNonEmpty("modifiedAt"),
        row.getStringNonEmpty("data")
    )

}
