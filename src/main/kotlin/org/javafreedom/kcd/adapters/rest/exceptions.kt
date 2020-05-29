package org.javafreedom.kcd.adapters.rest

class AuthenticationException(message: String?): Exception(message)
class AuthorizationException : RuntimeException()
class InvalidDataException : RuntimeException()
class MappingException : RuntimeException()
