package org.javafreedom.kcd.application.port.output.repository

/**
 * Exception thrown if Observation is not found in Repository using the given parameters
 */
class ObservationNotFoundException(message: String) : RuntimeException(message)
