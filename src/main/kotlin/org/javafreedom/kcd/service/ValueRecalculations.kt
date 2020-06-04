package org.javafreedom.kcd.service

import kotlin.math.roundToInt

object ValueRecalculations {

    val recalculations =
        mapOf(
            "mmol/l" to listOf<(value: Any?) -> Any>(ValueRecalculations::mmol)
        )

    fun mmol(value: Any?): Any {
        return when (value) {
            is Number -> value.toDouble().times(18.02).roundToInt()
            else -> -1
        }
    }

}


