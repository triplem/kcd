package org.javafreedom.kcd.adapters.persistence.cassandra.repository

import com.datastax.oss.driver.api.core.CqlSession
import com.github.nosan.embedded.cassandra.Cassandra
import com.github.nosan.embedded.cassandra.CassandraBuilder
import com.github.nosan.embedded.cassandra.commons.logging.Slf4jLogger
import com.github.nosan.embedded.cassandra.cql.CqlScript
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.time.Duration
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

open class RepositoryTest<R : AbstractCassandraRepository> {

    private lateinit var sut: R

    // this companion object is required to fulfill all necessary embedded-cassandra requirements
    companion object {
        @JvmStatic
        lateinit var testSession: CqlSession

        @JvmStatic
        lateinit var cassandra: Cassandra

        @JvmStatic
        fun beforeAll() {
            cassandra = CassandraBuilder()
                .startupTimeout(Duration.ofMinutes(3))
                .registerShutdownHook(true)
                .logger(Slf4jLogger(LoggerFactory.getLogger("Cassandra")))
                .addJvmOptions("-XX:ActiveProcessorCount=1")
                .addJvmOptions("-Xmx1G")
                .addEnvironmentVariable("MAX_HEAP_SIZE", "1024M")
                .addEnvironmentVariable("HEAP_NEWSIZE", "256M")
                .addConfigProperty("enable_materialized_views", true)
                .version("4.0-beta4")
                .build()
            cassandra.start()

            val settings = cassandra.settings
            val contactPoint = InetSocketAddress(settings.address, settings.port)
            val initializeSession = CqlSession.builder()
                .addContactPoint(contactPoint)
                .withLocalDatacenter("datacenter1")
                .build()

            CqlScript.ofClassPath("keyspace.cql").forEachStatement(initializeSession::execute)

            testSession = CqlSession.builder()
                .addContactPoint(contactPoint)
                .withLocalDatacenter("datacenter1")
                .withKeyspace("kcd")
                .build()
        }

        @JvmStatic
        fun prepareDatabase(cqlName: String) {
            CqlScript.ofClassPath(cqlName).forEachStatement(testSession::execute)
        }

        @JvmStatic
        fun afterAll() {
            testSession.close()
            cassandra.stop()
        }
    }

    inline fun <reified U : R> createRepository() {
        setSut(U::class, testSession)
    }

    fun <U : R> setSut(cls: KClass<U>, cqlSession: CqlSession) {
        sut = cls.primaryConstructor!!.call(cqlSession)
    }

    fun getSut() = sut

}