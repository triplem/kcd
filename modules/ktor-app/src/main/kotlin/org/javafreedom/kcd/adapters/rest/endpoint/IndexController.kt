package org.javafreedom.kcd.adapters.rest.endpoint

import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.ktor.controller.AbstractDIController

class IndexController(application: Application) : AbstractDIController(application) {

    override fun Route.getRoutes() {
        get<Index> {
            call.respondText("HELLO WORLD!", contentType = io.ktor.http.ContentType.Text.Plain)
        }
    }

}

//        authenticate {
//            val repo by kodein().instance<Repository>()
//
//            get("/") {
//                val principal = call.authentication.principal
//                log.debug("principal: {}", principal)
//                log.debug("repo: {}", repo)
//
//
//            }
//
//        }

//suspend fun ApplicationCall.respondRedirect(location: Any)
//        = respondRedirect(url = url(location), permanent = false)
