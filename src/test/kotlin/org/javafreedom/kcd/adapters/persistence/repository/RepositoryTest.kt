package org.javafreedom.kcd.adapters.persistence.repository

import com.datastax.oss.driver.api.core.CqlSession
import com.github.nosan.embedded.cassandra.api.connection.CqlSessionCassandraConnectionFactory
import com.github.nosan.embedded.cassandra.api.cql.CqlDataSet
import com.github.nosan.embedded.cassandra.junit5.test.CassandraExtension
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.function.Consumer
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

open class RepositoryTest<R : Repository> {

    private lateinit var sut: R

    // this companion object is required to fulfill all necessary embedded-cassandra requirements
    companion object {
        @JvmStatic
        var testSessionFactory = CqlSessionCassandraConnectionFactory()

        @JvmField
        @RegisterExtension
        var cassandraExtension = CassandraExtension().withCassandraFactory(
                com.github.nosan.embedded.cassandra.EmbeddedCassandraFactory().apply {
                    setArtifact(com.github.nosan.embedded.cassandra.artifact.Artifact.ofVersion("4.0-alpha4"))
                    timeout = Duration.of(3, ChronoUnit.MINUTES)
                    configProperties.putIfAbsent("enable_materialized_views", true)
                    jvmOptions.addAll(listOf("-XX:ActiveProcessorCount=1"))
                }
            )
            .withCassandraConnectionFactory(CqlSessionCassandraConnectionFactory())

        @BeforeAll
        @JvmStatic
        internal fun beforeAll() {
            val dataSet = CqlDataSet.ofClasspaths("keyspace.cql")
            val statements = dataSet.statements
            val connection = CqlSessionCassandraConnectionFactory()
                .create(cassandraExtension.cassandra).connection
            statements.forEach(Consumer { connection.execute(it) })

            testSessionFactory.sessionBuilderCustomizers +=
                Consumer { it.withKeyspace("kcd") }
        }

        @JvmStatic
        fun prepareDatabase(cqlName: String) {
            val dataSet = CqlDataSet.ofClasspaths(cqlName)
            val statements = dataSet.statements
            val connection = testSessionFactory
                .create(cassandraExtension.cassandra).connection
            statements.forEach(Consumer { connection.execute(it) })
        }
    }

    inline fun <reified U : R> createRepository() {
        val session = testSessionFactory.create(cassandraExtension.cassandra)
        setSut(U::class, session.connection)
    }

    fun <U: R> setSut(cls: KClass<U>, cqlSession: CqlSession) {
        sut = cls.primaryConstructor!!.call(cqlSession)
    }

    fun getSut() = sut

}