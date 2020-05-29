package org.javafreedom.kcd.adapters.rest.model

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Response(val description: String = "", val statusCode: Int = 200)
