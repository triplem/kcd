package org.javafreedom.kcd.adapters.rest.model

import io.ktor.http.*

class MappingException(message: String, cause: Throwable) : RuntimeException(message, cause)

class HttpException(val code: HttpStatusCode, val description: String = code.description) :
    RuntimeException(description)
