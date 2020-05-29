package org.javafreedom.kcd.adapters.rest.endpoint

import io.ktor.locations.*

@SuppressWarnings("detekt:EmptyDefaultConstructor")
@Location("/")
class Index

@SuppressWarnings("detekt:EmptyDefaultConstructor")
@Location("/observation")
class ObservationIndex {

    @Location("/{observationId}")
    class ById(val parent: ObservationIndex, val observationId: String)

}
