package org.javafreedom.kcd.adapters.rest.model

import io.ktor.http.*

class MappingException : RuntimeException()

class HttpException(val code: HttpStatusCode, val description: String = code.description) :
    RuntimeException(description)
