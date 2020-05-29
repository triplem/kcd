package org.javafreedom.kcd.adapters.persistence.memory

import org.javafreedom.kcd.application.port.output.repository.DeleteObservationPort
import org.javafreedom.kcd.application.port.output.repository.FindObservationPort
import org.javafreedom.kcd.application.port.output.repository.SaveObservationPort
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val memoryModule = DI.Module(name = "persistenceModule") {
    bind<MemoryObservationAdapter>() with singleton { MemoryObservationAdapter() }
    bind<SaveObservationPort>() with singleton { instance<MemoryObservationAdapter>() }
    bind<DeleteObservationPort>() with singleton { instance<MemoryObservationAdapter>() }
    bind<FindObservationPort>() with singleton { instance<MemoryObservationAdapter>() }
}
