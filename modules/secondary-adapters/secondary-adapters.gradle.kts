description = "secondary-adapters"

val kodeinVersion: String by project
val logbackVersion: String by project
val datastaxVersion: String by project
val junitVersion: String by project
val kotlinCoroutinesVersion: String by project
val tinkerpopVersion: String by project
val mockkVersion: String by project
val assertkVersion: String by project
val uuidCreatorVersion: String by project
val kotlinxSerializationVersion: String by project

plugins {
    id("org.javafreedom.kotlin-library-conventions")

    kotlin("plugin.serialization")
    kotlin("kapt")
}

dependencies {
    implementation(project(":application-core"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinCoroutinesVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinCoroutinesVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")

    implementation("io.github.microutils:kotlin-logging:2.0.3")

    implementation("org.kodein.di:kodein-di:$kodeinVersion")

    implementation("com.github.f4b6a3:uuid-creator:$uuidCreatorVersion")

    implementation("com.datastax.oss:java-driver-core:$datastaxVersion")
    implementation("com.datastax.oss:java-driver-query-builder:$datastaxVersion")
    implementation("io.netty:netty-handler:4.1.63.Final")
    implementation("org.apache.tinkerpop:gremlin-core:$tinkerpopVersion")
    implementation("org.apache.tinkerpop:gremlin-driver:$tinkerpopVersion")
    implementation("org.apache.tinkerpop:tinkergraph-gremlin:$tinkerpopVersion")

    testImplementation("com.willowtreeapps.assertk:assertk-jvm:$assertkVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")

    testIntegrationImplementation("com.github.nosan:embedded-cassandra:4.0.1")
    testIntegrationImplementation("ch.qos.logback:logback-classic:$logbackVersion")
    testIntegrationImplementation("org.slf4j:slf4j-api:1.7.30")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

// https://youtrack.jetbrains.com/issue/KT-34901
//kotlin.target.compilations.getByName("integrationTest") {
//    associateWith(target.compilations.getByName(SourceSet.MAIN_SOURCE_SET_NAME))
//}
