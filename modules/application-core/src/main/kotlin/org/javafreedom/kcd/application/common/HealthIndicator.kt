package org.javafreedom.kcd.application.common

interface HealthIndicator {

    fun isReady(): Pair<String, Boolean>

}
