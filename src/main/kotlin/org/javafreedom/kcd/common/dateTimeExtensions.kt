package org.javafreedom.kcd.common

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

fun Instant.toZonedDateTime(): ZonedDateTime {
    return ZonedDateTime.ofInstant(this, ZoneId.of("UTC"))
}
