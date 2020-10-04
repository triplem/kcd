package org.javafreedom.kcd.service

import kotlin.math.roundToInt

object ValueRecalculations {
    const val MMOL_TO_MGDL = 18.02

    val recalculations =
        mapOf(
            "mmol/l" to listOf<(value: Any?) -> Any>(ValueRecalculations::mmol)
        )

    fun mmol(value: Any?): Any {
        println(value.toString())
        return when (value) {
            is Number -> value.toDouble().times(MMOL_TO_MGDL).roundToInt()
            else -> -1
        }
    }

}


