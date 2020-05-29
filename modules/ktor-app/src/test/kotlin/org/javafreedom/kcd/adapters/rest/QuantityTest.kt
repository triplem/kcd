package org.javafreedom.kcd.adapters.rest

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class QuantityTest {

    @Test
    fun test() {
        assertThat((100.mgdl to mmol).amount).isEqualTo(5.6)
        assertThat((5.6.mmol to mgdl).amount).isEqualTo(100)
        assertThat((100.mgdl to mmol to mgdl).amount).isEqualTo(100)
    }

}