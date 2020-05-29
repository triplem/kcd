package org.javafreedom.kcd.adapters.rest.model

import io.ktor.http.*

class AuthenticationException(message: String?) : RuntimeException(message)
class AuthorizationException : RuntimeException()
class InvalidDataException : RuntimeException()
class MappingException : RuntimeException()
class ConversionException : RuntimeException()

class HttpException(val code: HttpStatusCode, val description: String = code.description) :
    RuntimeException(description)

fun httpException(code: HttpStatusCode, message: String = code.description): Nothing =
    throw HttpException(code, message)

fun httpException(code: Int, message: String = "Error $code"): Nothing =
    throw HttpException(HttpStatusCode(code, message))

