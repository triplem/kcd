package org.javafreedom.kcd.adapters.rest

import org.javafreedom.kcd.adapters.rest.model.MappingException
import kotlin.math.pow
import kotlin.math.roundToInt

fun Double.roundTo(numFractionDigits: Int): Double {
    val factor = 10.0.pow(numFractionDigits.toDouble())
    return (this * factor).roundToInt() / factor
}

val mmol = "mmol"
val mgdl = "mgdl"

class Value(val name: String, val amount: Number) {

    infix fun to(targetName: String): Value {
        return if (targetName.equals(name)) {
            this
        } else {
            when (targetName) {
                "mmol" -> Value(targetName, (this.amount as Int).times(0.0555).roundTo(1))
                "mgdl" -> Value(targetName, (this.amount as Double).times(18.02).toInt())
                else -> throw MappingException()
            }
        }
    }
}

val Int.mgdl get() = Value("mgdl", this)
val Double.mmol get() = Value("mmol", this)
