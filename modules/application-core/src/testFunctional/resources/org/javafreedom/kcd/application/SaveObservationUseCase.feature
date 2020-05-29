Feature: KCD Observations

  Scenario: User would like to save an Observation
    Given a valid observation
    When the user saves the observation
    Then a random UUID is returned