package org.javafreedom.kcd.domain.model

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.assertThrows
import java.time.ZonedDateTime
import kotlin.test.Test

typealias BloodSugar = Element

class ReportTest {

    @Test
    fun testEmptyList() {
        val measurement: List<BloodSugar> = emptyList()

        assertThrows<ReportException> {
            val report = BloodSugarReport(
                "BloodSugar", ZonedDateTime.now(), ZonedDateTime.now(),
                70, 180, measurement
            )
        }
    }

    @Test
    fun testOneEntry() {
        val measurement: List<BloodSugar> = listOf(
            BloodSugar(
                type = "BloodSugar",
                quantity = Quantity(unit = "mg/dl", amount = 100),
                device = "TEST",
                extension = ""
            )
        )

        val report = BloodSugarReport(
            "BloodSugar", ZonedDateTime.now(), ZonedDateTime.now(),
            70, 180, measurement
        )

        assertThat(report.gvi, "GVI").isEqualTo(0.5)
        assertThat(report.pgs, "PGS").isEqualTo(0.0)
        assertThat(report.average, "Average").isEqualTo(100)
        assertThat(report.stdDev, "StdDev").isEqualTo(0.0)
        assertThat(report.median, "Median").isEqualTo(100)

        assertThat(report.noReadings).isEqualTo(1)
        assertThat(report.inRangeReadings).isEqualTo(1)
        assertThat(report.inRangePercent).isEqualTo(100)
        assertThat(report.highReadings).isEqualTo(0)
        assertThat(report.highPercent).isEqualTo(0)
        assertThat(report.lowReadings).isEqualTo(0)
        assertThat(report.lowPercent).isEqualTo(0)
        assertThat(report.min).isEqualTo(100)
        assertThat(report.max).isEqualTo(100)


    }

    val measurements = listOf(
        BloodSugar(
            type = "BloodSugar",
            quantity = Quantity(unit = "mg/dl", amount = 100),
            device = "TEST",
            extension = ""
        ),
        BloodSugar(
            type = "BloodSugar",
            quantity = Quantity(unit = "mg/dl", amount = 180),
            device = "TEST",
            extension = ""
        ),
        BloodSugar(
            type = "BloodSugar",
            quantity = Quantity(unit = "mg/dl", amount = 186),
            device = "TEST",
            extension = ""
        ),
        BloodSugar(
            type = "BloodSugar",
            quantity = Quantity(unit = "mg/dl", amount = 183),
            device = "TEST",
            extension = ""
        ),
        BloodSugar(
            type = "BloodSugar",
            quantity = Quantity(unit = "mg/dl", amount = 170),
            device = "TEST",
            extension = ""
        ),
        BloodSugar(
            type = "BloodSugar",
            quantity = Quantity(unit = "mg/dl", amount = 110),
            device = "TEST",
            extension = ""
        ),
        BloodSugar(
            type = "BloodSugar",
            quantity = Quantity(unit = "mg/dl", amount = 112),
            device = "TEST",
            extension = ""
        ),
        BloodSugar(
            type = "BloodSugar",
            quantity = Quantity(unit = "mg/dl", amount = 94),
            device = "TEST",
            extension = ""
        ),
        BloodSugar(
            type = "BloodSugar",
            quantity = Quantity(unit = "mg/dl", amount = 70),
            device = "TEST",
            extension = ""
        ),
        BloodSugar(
            type = "BloodSugar",
            quantity = Quantity(unit = "mg/dl", amount = 65),
            device = "TEST",
            extension = ""
        ),
        BloodSugar(
            type = "BloodSugar",
            quantity = Quantity(unit = "mg/dl", amount = 57),
            device = "TEST",
            extension = ""
        ),
        BloodSugar(
            type = "BloodSugar",
            quantity = Quantity(unit = "mg/dl", amount = 54),
            device = "TEST",
            extension = ""
        ),
        BloodSugar(
            type = "BloodSugar",
            quantity = Quantity(unit = "mg/dl", amount = 80),
            device = "TEST",
            extension = ""
        ),
        BloodSugar(
            type = "BloodSugar",
            quantity = Quantity(unit = "mg/dl", amount = 81),
            device = "TEST",
            extension = ""
        )
    )

    @Test
    fun testMultipleEntries() {
        val report = BloodSugarReport(
            type = "BloodSugar", ZonedDateTime.now(), ZonedDateTime.now(),
            70, 180, measurements
        )

        assertThat(report.gvi, "GVI").isEqualTo(3.56)
        assertThat(report.pgs, "PGS").isEqualTo(391.6)
        assertThat(report.average, "Average").isEqualTo(110)
        assertThat(report.stdDev, "StdDev").isEqualTo(47.27)
        assertThat(report.median, "Median").isEqualTo(97)

        assertThat(report.noReadings, "noReadings").isEqualTo(14)
        assertThat(report.inRangeReadings, "inRangeReadings").isEqualTo(9)
        assertThat(report.inRangePercent, "inRangePercent").isEqualTo(63)
        assertThat(report.highReadings, "highReadings").isEqualTo(2)
        assertThat(report.highPercent, "highPercent").isEqualTo(14)
        assertThat(report.lowReadings, "lowReadings").isEqualTo(3)
        assertThat(report.lowPercent, "lowPercent").isEqualTo(21)
        assertThat(report.min, "min").isEqualTo(54)
        assertThat(report.max, "max").isEqualTo(186)

        assertThat(
            report.lowReadings
                    + report.inRangeReadings
                    + report.highReadings, "noReadings Calculated"
        )
            .isEqualTo(14)
    }
}