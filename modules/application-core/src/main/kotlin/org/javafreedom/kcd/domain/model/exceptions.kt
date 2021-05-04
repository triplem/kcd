package org.javafreedom.kcd.domain.model

open class BusinessException(message: String) : RuntimeException(message)
class ReportException(message: String) : BusinessException(message)

/**
 * Exception thrown if Observation is not found in Repository using the given parameters
 */
class ObservationNotFoundException(message: String) : BusinessException(message)
