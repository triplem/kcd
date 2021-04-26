import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
    implementation(kotlin("gradle-plugin", "1.4.30"))
    implementation(kotlin("bom", "1.4.30"))
    implementation(kotlin("reflect", "1.4.30"))
    implementation(kotlin("stdlib-jdk8", "1.4.30"))
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.4.20") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
    }
    implementation("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:3.1.1")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.16.0") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
    }
    implementation("org.owasp:dependency-check-gradle:6.1.0")
    implementation("org.asciidoctor:asciidoctor-gradle-jvm:3.2.0")
    implementation("com.bmuschko:gradle-docker-plugin:6.7.0")
}
