package org.javafreedom.kcd.adapters.persistence

import com.datastax.oss.driver.api.core.CqlSession
import io.ktor.application.Application
import org.javafreedom.kcd.common.subKodein
import org.javafreedom.kcd.adapters.persistence.repository.ObservationRepository
import org.javafreedom.kcd.adapters.persistence.repository.UserTypeRepository
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

fun Application.persistenceModule() {

    subKodein {
        bind<CqlSession>() with singleton { cassandraSession() }
        bind<ObservationRepository>() with singleton { ObservationRepository(instance()) }
        bind<UserTypeRepository>() with singleton { UserTypeRepository(instance()) }
    }

}
