package org.javafreedom.kcd.steps

import assertk.assertThat
import io.cucumber.java.en.Then
import org.javafreedom.kcd.state.ScenarioState

class ResultSteps(val state: ScenarioState) {

    @Then("a random UUID is returned")
    fun uuidReturned() {
        assertThat { state.result != null }
    }

}