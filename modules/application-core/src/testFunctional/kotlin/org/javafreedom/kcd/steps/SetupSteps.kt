package org.javafreedom.kcd.steps

import io.cucumber.java.en.Given
import org.javafreedom.kcd.domain.model.EmbeddedAudit
import org.javafreedom.kcd.domain.model.Element
import org.javafreedom.kcd.domain.model.Observation
import org.javafreedom.kcd.domain.model.Quantity
import org.javafreedom.kcd.state.ScenarioState
import java.time.Instant

class SetupSteps(val state: ScenarioState) {

    @Given("a valid observation")
    fun createValidObservation() {
        state.observation = Observation(
            date = Instant.now(),
            audit = EmbeddedAudit("test", Instant.now(), Instant.now()),
            element = Element("test", "test", Quantity("test", 100), "test", "test"),
            component = null
        )
    }

}