package org.javafreedom.kcd.domain.model

import java.time.ZonedDateTime
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.math.sqrt

fun Double.roundToDecimals(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

/**
 *
 */
interface ObservationReport {

    val type: String
    val fromDate: ZonedDateTime
    val toDate: ZonedDateTime
    val observationElements: List<Element>

}

class BloodSugarReport(
    override val type: String,
    override val fromDate: ZonedDateTime,
    override val toDate: ZonedDateTime,
    val rangeLow: Int,
    val rangeHigh: Int,
    override val observationElements: List<Element>
) : ObservationReport {

    init {
        if (this.observationElements.isNullOrEmpty()) throw ReportException("no report data found")
    }

    private val measures = this.observationElements.map { it.quantity.amount as Int }
    private val size = this.observationElements.size

    val average: Int
        get() = this.measures.average().roundToInt()

    val stdDev: Double
        get() = this.measures
            .fold(0.0, { accumulator, next -> accumulator + (next - average.toDouble()).pow(2.0) })
            .let { sqrt(it / size) }.roundToDecimals(2)

    val median: Int
        get() = this.measures
            .sorted().let { (it[it.size / 2] + it[(it.size - 1) / 2]) / 2 }

    val noReadings: Int
        get() = this.size

    val lowReadings: Int
        get() = this.measures.filter { it < rangeLow }.size

    val lowPercent: Int
        get() = lowReadings * (100 / this.size)

    val highReadings: Int
        get() = this.measures.filter { it > rangeHigh }.size

    val highPercent: Int
        get() = highReadings * (100 / this.size)

    val inRangeReadings: Int
        get() = this.measures
            .filter { it in rangeLow..rangeHigh }
            .size

    val inRangePercent: Int
        get() = inRangeReadings * (100 / this.size)

    val hba1cDcct: Int
        get() = (10 * (this.average + 46.7) / 28.7).roundToInt() / 10

    val hba1cIFCC: Int
        get() = (((this.average + 46.7) / 28.7 - 2.15) * 10.929).roundToInt()

    val gvi: Double
        get() {
            val gviDelta = Math.abs(this.measures.last() - this.measures.first()).toDouble()
            val gviIdeal = Math.sqrt(
                Math.pow((5.0).times(this.measures.size + 1), 2.0)
                        + Math.pow(gviDelta, 2.0)
            )

            val measureIterator = this.measures.listIterator()
            // this is just to skip first entry in loop
            var gviTotal = 0.0
            var last = this.measures.first()
            while (measureIterator.hasNext()) {
                val current = measureIterator.next()
                val delta: Double = (current.minus(last)).toDouble()
                gviTotal += sqrt(25 + delta.pow(2.0))

                last = current
            }

            return ((gviTotal / gviIdeal * 100) / 100).roundToDecimals(2)
        }

    val pgs: Double
        get() {
            return (gvi * average * (1 - (inRangePercent / 100)) * 100) / 100
        }

    val min: Int?
        get() = this.measures.minByOrNull { it }

    val max: Int?
        get() = this.measures.maxByOrNull { it }

}
