package org.javafreedom.kcd.adapters.persistence.cassandra

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.CqlSessionBuilder
import org.javafreedom.kcd.adapters.persistence.cassandra.repository.ObservationRepository
import org.javafreedom.kcd.application.port.output.repository.DeleteObservationPort
import org.javafreedom.kcd.application.port.output.repository.FindObservationPort
import org.javafreedom.kcd.application.port.output.repository.SaveObservationPort
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val cassandraModule = DI.Module(name = "persistenceModule") {
    bind<CqlSession>() with singleton { CqlSessionBuilder().build() }
    bind<ObservationRepository>() with singleton { ObservationRepository(instance()) }

    bind<CassandraObservationAdapter>() with singleton { CassandraObservationAdapter(instance()) }
    bind<SaveObservationPort>() with singleton { instance<CassandraObservationAdapter>() }
    bind<DeleteObservationPort>() with singleton { instance<CassandraObservationAdapter>() }
    bind<FindObservationPort>() with singleton { instance<CassandraObservationAdapter>() }
}
