description = "ktor-app"

val kodeinVersion: String by project
val junitVersion: String by project
val spekVersion: String by project
val kotlinCoroutinesVersion: String by project
val logbackVersion: String by project
val kotlinxSerializationVersion: String by project
val mockkVersion: String by project
val assertkVersion: String by project
val uuidCreatorVersion: String by project
val kloggingVersion: String by project
val ktorVersion: String by project

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
    kotlin("plugin.serialization")
    jacoco
}

//repositories {
//    maven { url = uri("https://jitpack.io") }
//}

dependencies {
    implementation(project(":application-core"))
    implementation(project(":secondary-adapters"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinCoroutinesVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")

    // logging
    implementation("io.github.microutils:kotlin-logging:$kloggingVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$kotlinCoroutinesVersion")
    runtimeOnly("ch.qos.logback:logback-classic:$logbackVersion")
    testRuntimeOnly("co.elastic.logging:logback-ecs-encoder:0.4.0")

    // ktor platform
    implementation(platform(ktor("bom:$ktorVersion")))
    implementation(ktor("server-cio"))
    implementation(ktor("server-core"))
    implementation(ktor("server-host-common"))
    implementation(ktor("auth"))
    implementation(ktor("auth-jwt"))
    implementation(ktor("serialization"))
    implementation(ktor("locations"))
    implementation("com.auth0:jwks-rsa:0.17.1")

    // kodein DI
    implementation("org.kodein.di:kodein-di-conf:$kodeinVersion")
    implementation("org.kodein.di:kodein-di:$kodeinVersion")
    implementation("org.kodein.di:kodein-di-framework-ktor-server-controller-jvm:$kodeinVersion")

    // test and integration test
//    implementation("com.github.zensum:ktor-health-check:011a5a8")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testImplementation(ktor("server-tests"))
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:$assertkVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinCoroutinesVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")

    // acceptance test (some are duplicate to test, but we do have an own classpath here)
    testAcceptanceImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
    testAcceptanceImplementation("com.github.f4b6a3:uuid-creator:$uuidCreatorVersion")
    testAcceptanceImplementation(kotlin("test"))
    testAcceptanceImplementation(kotlin("test-junit5"))
    testAcceptanceImplementation(ktor("server-tests"))

    // define any required OkHttp artifacts without version
    testAcceptanceImplementation("com.squareup.okhttp3:okhttp:4.9.0")

    testAcceptanceImplementation("com.willowtreeapps.assertk:assertk-jvm:$assertkVersion")
    testAcceptanceRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
    testAcceptanceRuntimeOnly("ch.qos.logback:logback-classic:$logbackVersion")

    // spek requires kotlin-reflect, can be omitted if already in the classpath
    testAcceptanceRuntimeOnly(kotlin("reflect"))
}

// add the spek engine to the acceptanceTest task
tasks.acceptanceTest {
    useJUnitPlatform {
        includeEngines.add("spek2")
    }
}
