import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

val properties = Properties().also { props ->
    project.projectDir.resolveSibling("gradle.properties").bufferedReader().use {
        props.load(it)
    }
}
val kotlinVersion: String = properties.getProperty("kotlinVersion")
val owaspVersion: String = properties.getProperty("owaspVersion")

plugins {
    // Support convention plugins written in Kotlin. Convention plugins are build scripts in 'src/main' that automatically become available as plugins in the main build.
    `kotlin-dsl`
}

repositories {
    // Use the plugin portal to apply community plugins in convention plugins.
    gradlePluginPortal()
    jcenter()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "11"
}

dependencies {
    implementation(kotlin("gradle-plugin", kotlinVersion))
    implementation(kotlin("bom", kotlinVersion))
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:$kotlinVersion") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
    }
    implementation("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:3.1.1")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.16.0") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
    }
    implementation("org.owasp:dependency-check-gradle:$owaspVersion")
    implementation("org.asciidoctor:asciidoctor-gradle-jvm:3.2.0")
    implementation("com.bmuschko:gradle-docker-plugin:6.7.0")

}
