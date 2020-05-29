package org.javafreedom.kcd

import io.cucumber.junit.platform.engine.Cucumber

/**
 * Gradle does not support JUnit platform discovery selectors yet. This class helps Gradle to discover all tests.
 *
 * In order to run scenarios, use 'gradle test'. Executing this class in your IDE won't work.
 *
 * @see <a href="https://github.com/gradle/gradle/issues/4773">Gradle 4773</a>
 *
 * copied from https://github.com/cronn/cucumber-junit5-example/blob/master/src/test/java/com/example/BuildToolSupport.java
 */
@Cucumber
class CucumberTestLauncher {
}