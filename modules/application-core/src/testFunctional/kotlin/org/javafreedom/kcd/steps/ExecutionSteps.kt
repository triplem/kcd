package org.javafreedom.kcd.steps

import io.cucumber.java.en.When
import io.mockk.every
import io.mockk.mockk
import org.javafreedom.kcd.application.port.output.repository.SaveObservationPort
import org.javafreedom.kcd.state.ScenarioState
import java.util.*

class ExecutionSteps(val state: ScenarioState) {

    val saveObservationPort = mockk<SaveObservationPort>()

    @When("the user saves the observation")
    fun userSavesObservation() {
        every { saveObservationPort.saveObservation(any()) } returns UUID.randomUUID()
        state.result = saveObservationPort.saveObservation(state.observation)
    }

}