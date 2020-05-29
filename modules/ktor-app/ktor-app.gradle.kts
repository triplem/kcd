description = "ktor-app"

val kodein_version: String by project
val junit_version: String by project
val spek_version: String by project

/**
 * Builds the dependency notation for the named Ktor [module] at the given [version].
 *
 * @param module simple name of the Kotlin module, for example "reflect".
 * @param version optional desired version, unspecified if null.
 */
fun DependencyHandler.ktor(module: String, version: String? = null): Any =
    "io.ktor:ktor-$module${version?.let { ":$version" } ?: ""}"

plugins {
    id("org.javafreedom.kotlin-application-conventions")

    kotlin("jvm")
    kotlin("plugin.serialization") version "1.4.30"
    jacoco
}

repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(project(":application-core"))
    implementation(project(":secondary-adapters"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.4.3")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")

    // logging
    implementation("io.github.microutils:kotlin-logging:2.0.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:1.4.3")
    runtimeOnly("ch.qos.logback:logback-classic:1.2.3")
    testRuntimeOnly("co.elastic.logging:logback-ecs-encoder:0.4.0")

    // ktor platform
    implementation(platform(ktor("bom:1.5.1")))
    implementation(ktor("server-cio"))
    implementation(ktor("server-core"))
    implementation(ktor("server-host-common"))
    implementation(ktor("auth"))
    implementation(ktor("auth-jwt"))
    implementation(ktor("serialization"))
    implementation(ktor("locations"))

    // kodein DI
    implementation("org.kodein.di:kodein-di-conf:$kodein_version")
    implementation("org.kodein.di:kodein-di:$kodein_version")
    implementation("org.kodein.di:kodein-di-framework-ktor-server-controller-jvm:$kodein_version")

    // test and integration test
    implementation("com.github.zensum:ktor-health-check:011a5a8")
    testImplementation("io.mockk:mockk:1.10.0")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testImplementation(ktor("server-tests"))
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.23.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit_version")

    // acceptance test (some are duplicate to test, but we do have an own classpath here)
    testAcceptanceImplementation("org.spekframework.spek2:spek-dsl-jvm:$spek_version")
    testAcceptanceImplementation("com.github.f4b6a3:uuid-creator:3.5.0")
    testAcceptanceImplementation(kotlin("test"))
    testAcceptanceImplementation(kotlin("test-junit5"))
    testAcceptanceImplementation(ktor("server-tests"))

    // define any required OkHttp artifacts without version
    testAcceptanceImplementation("com.squareup.okhttp3:okhttp:4.9.0")

    testAcceptanceImplementation("com.willowtreeapps.assertk:assertk-jvm:0.23.1")
    testAcceptanceRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spek_version")
    testAcceptanceRuntimeOnly("ch.qos.logback:logback-classic:1.2.3")

    // spek requires kotlin-reflect, can be omitted if already in the classpath
    testAcceptanceRuntimeOnly(kotlin("reflect"))
}

// add the spek engine to the acceptanceTest task
tasks.acceptanceTest {
    useJUnitPlatform {
        includeEngines.add("spek2")
    }
}
