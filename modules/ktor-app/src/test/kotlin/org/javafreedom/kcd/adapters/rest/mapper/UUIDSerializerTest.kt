package org.javafreedom.kcd.adapters.rest.mapper

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import java.util.*

class UUIDSerializerTest {

    @Test
    fun uuidFromString() {
        val source = "52d292ce-0b7e-4882-905c-de8c75791b8c"

        val uuid = UUID.fromString(source)
        val result = uuid.toString()

        assertThat(result).isEqualTo(source)
    }
}