package org.javafreedom.kcd.adapters.persistence.cassandra.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import com.datastax.oss.driver.api.core.cql.Row
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Instant
import java.util.*

@ExtendWith(MockKExtension::class)
class RowExtensionsTest {

    @MockK
    lateinit var row: Row

    @Test
    fun getUuidNonEmpty_Ok() {
        val uuid = UUID.randomUUID()
        every { row.getUuid("name") } returns uuid

        assertThat(row.getUuidNonEmpty("name")).isEqualTo(uuid)
    }

    @Test
    fun getUuidNonEmpty_null() {
        every { row.getUuid("name") } returns null

        assertThat {
            row.getUuidNonEmpty("name")
        }.isFailure().isInstanceOf(InvalidIdException::class)
    }

    @Test
    fun getStringNonEmpty_Ok() {
        val result = "result"
        every { row.getString("name") } returns result

        assertThat(row.getStringNonEmpty("name")).isEqualTo(result)
    }

    @Test
    fun getStringNonEmpty_null() {
        val result = ""
        every { row.getString("name") } returns null

        assertThat(row.getStringNonEmpty("name")).isEqualTo(result)
    }

    @Test
    fun getInstantNonEmpty_Ok() {
        val result = Instant.now()
        every { row.getInstant("name") } returns result

        assertThat(row.getInstantNonEmpty("name")).isEqualTo(result)
    }

    @Test
    fun getInstantNonEmpty_null() {
        val result = Instant.MIN
        every { row.getInstant("name") } returns null

        assertThat(row.getInstantNonEmpty("name")).isEqualTo(result)
    }

}