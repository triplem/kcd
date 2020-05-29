package org.javafreedom.verification

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

plugins {
    kotlin("jvm")
    id("idea")
}

// -----------------------------
// Add testIntegration Sourceset and Task (and also add those as Test-Module to IDEA)
// -----------------------------

// to get rid of "Overload resolution ambiguity"-messsage
val sourcesets = project.extensions.getByType(SourceSetContainer::class)
val testIntegration by sourcesets.creating

configurations[testIntegration.implementationConfigurationName]
    .extendsFrom(configurations.testImplementation.get())
configurations[testIntegration.runtimeOnlyConfigurationName]
    .extendsFrom(configurations.testRuntimeOnly.get())

// this the solution for the deprecation?
configurations[testIntegration.implementationConfigurationName].isCanBeResolved = true
configurations[testIntegration.runtimeOnlyConfigurationName].isCanBeResolved = true

val koTarget: KotlinTarget = kotlin.target
koTarget.compilations.named("testIntegration") {
    associateWith(target.compilations.named("main").get())
}

val integrationTestTask = tasks.register<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "Verification"

    testClassesDirs = testIntegration.output.classesDirs
    classpath =
        configurations[testIntegration.runtimeClasspathConfigurationName] + testIntegration.output

    shouldRunAfter(tasks.test)
}

// -----------------------------
// Add testAcceptance Sourceset and Task (and also add those as Test-Module to IDEA)
// -----------------------------
// to get rid of "Overload resolution ambiguity"-messsage
val testAcceptance by sourcesets.creating

configurations[testAcceptance.implementationConfigurationName]
    .extendsFrom(configurations.implementation.get())
configurations[testAcceptance.runtimeOnlyConfigurationName]
    .extendsFrom(configurations.runtimeOnly.get())

// this the solution for the deprecation?
configurations[testAcceptance.implementationConfigurationName].isCanBeResolved = true
configurations[testAcceptance.runtimeOnlyConfigurationName].isCanBeResolved = true

koTarget.compilations.named("testAcceptance") {
    associateWith(target.compilations.named("main").get())
}

val acceptanceTestTask = tasks.register<Test>("acceptanceTest") {
    description = "Runs acceptance tests."
    group = "Verification"

    testClassesDirs = testAcceptance.output.classesDirs
    classpath =
        configurations[testAcceptance.runtimeClasspathConfigurationName] + testAcceptance.output

    shouldRunAfter(integrationTestTask)
}

tasks.check {
    dependsOn(integrationTestTask)
    dependsOn(acceptanceTestTask)
}

idea {
    module {
        val testSources = testSourceDirs
        testSources.addAll(testIntegration.allJava.srcDirs)
        testSources.addAll(testAcceptance.allJava.srcDirs)
        testSourceDirs = testSources

        val testResources = testResourceDirs
        testResources.addAll(testIntegration.resources.srcDirs)
        testResources.addAll(testAcceptance.resources.srcDirs)
        testResourceDirs = testResources
    }
}

// -----------------------------
// Add configuration to allow aggregation of unit-test-reports
// -----------------------------

// Share the test report data to be aggregated for the whole project
configurations.create("binaryTestResultsElements") {
    isVisible = false
    isCanBeResolved = false
    isCanBeConsumed = true
    extendsFrom(configurations.implementation.get())
    attributes {
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
        attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named("test-report-data"))
    }

    outgoing.artifact(tasks.test.map { task -> task.binaryResultsDirectory.get() })
    outgoing.artifact(integrationTestTask.map { task -> task.binaryResultsDirectory.get() })
    outgoing.artifact(acceptanceTestTask.map { task -> task.binaryResultsDirectory.get() })
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        useJUnitPlatform()
        events = setOf(
            TestLogEvent.FAILED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED,
            TestLogEvent.STANDARD_OUT
        )
        exceptionFormat = TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}
