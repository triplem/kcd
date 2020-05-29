package org.javafreedom.kcd.ktor

import org.javafreedom.kcd.application.port.input.DeleteObservationUseCase
import org.javafreedom.kcd.application.port.input.LoadObservationUseCase
import org.javafreedom.kcd.application.port.input.SaveObservationUseCase
import org.javafreedom.kcd.application.service.ObservationService
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val serviceModule = DI.Module("serviceModule") {
    bind<ObservationService>() with singleton {
        ObservationService(instance(), instance(), instance())
    }

    bind<SaveObservationUseCase>() with singleton { instance<ObservationService>() }
    bind<LoadObservationUseCase>() with singleton { instance<ObservationService>() }
    bind<DeleteObservationUseCase>() with singleton { instance<ObservationService>() }

}
