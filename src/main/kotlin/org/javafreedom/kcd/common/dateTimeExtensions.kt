package org.javafreedom.kcd.common

import java.time.*

fun LocalDateTime.toInstant(): Instant {
    return this.toInstant(ZoneOffset.UTC)
}

fun Instant.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(this, ZoneId.of("UTC"))
}

fun Instant.toZonedDateTime(): ZonedDateTime {
    return ZonedDateTime.ofInstant(this, ZoneId.of("UTC"))
}
