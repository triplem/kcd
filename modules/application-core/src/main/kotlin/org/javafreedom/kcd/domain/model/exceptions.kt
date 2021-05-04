package org.javafreedom.kcd.domain.model

open class BusinessException(message: String) : RuntimeException(message)
class ReportException(message: String) : BusinessException(message)
