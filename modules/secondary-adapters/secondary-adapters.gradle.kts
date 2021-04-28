description = "secondary-adapters"

val datastax_version: String by project
val junit_version: String by project
val kodein_version: String by project

plugins {
    id("org.javafreedom.kotlin-library-conventions")

    kotlin("plugin.serialization") version "1.4.32"
    kotlin("kapt")
}

dependencies {
    implementation(project(":application-core"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.4.3")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.3")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")

    implementation("io.github.microutils:kotlin-logging:2.0.3")

    implementation("org.kodein.di:kodein-di:7.2.0")

    implementation("com.github.f4b6a3:uuid-creator:3.5.0")

    implementation("com.datastax.oss:java-driver-core:$datastax_version")
    implementation("com.datastax.oss:java-driver-query-builder:$datastax_version")
    implementation("io.netty:netty-handler:4.1.63.Final")
    implementation("org.apache.tinkerpop:gremlin-core:3.4.10")
    implementation("org.apache.tinkerpop:gremlin-driver:3.4.10")
    implementation("org.apache.tinkerpop:tinkergraph-gremlin:3.4.10")

    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.23")
    testImplementation("io.mockk:mockk:1.10.0")
    testImplementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit_version")

    testIntegrationImplementation("com.github.nosan:embedded-cassandra:4.0.1")
    testIntegrationImplementation("ch.qos.logback:logback-classic:1.2.3")
    testIntegrationImplementation("org.slf4j:slf4j-api:1.7.30")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit_version")
}

// https://youtrack.jetbrains.com/issue/KT-34901
//kotlin.target.compilations.getByName("integrationTest") {
//    associateWith(target.compilations.getByName(SourceSet.MAIN_SOURCE_SET_NAME))
//}
