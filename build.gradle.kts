import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.asciidoctor.gradle.AsciidoctorTask
import org.sonarqube.gradle.SonarQubeTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

val logback_version: String by project
val datastax_version: String by project
val kodein_version: String by project
val junit_version: String by project

/**
 * Builds the dependency notation for the named Ktor [module] at the given [version].
 *
 * @param module simple name of the Kotlin module, for example "reflect".
 * @param version optional desired version, unspecified if null.
 */
fun DependencyHandler.ktor(module: String, version: String? = null): Any =
    "io.ktor:ktor-$module${version?.let { ":$version" } ?: ""}"


plugins {
    kotlin("jvm") version "1.3.70"
    kotlin("plugin.serialization") version "1.3.70"
    application
    id("com.bmuschko.docker-java-application") version "6.4.0"
    id("org.asciidoctor.convert") version "1.5.9.2"
    id("org.sonarqube") version "2.8"
    jacoco
    id("io.gitlab.arturbosch.detekt") version "1.8.0"
}

group = "org.javafreedom.kcd"
version = "0.0.1"

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "io.ktor.server.cio.EngineMain"
}

docker {
    javaApplication {
        baseImage.set("openjdk:11-jre-alpine")
        maintainer.set("Markus M. May 'mmay@somewhere.com'")
        jvmArgs.set(listOf("-server", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap",
            "-XX:InitialRAMFraction=2", "-XX:MinRAMFraction=2", "-XX:MaxRAMFraction=2", "-XX:+UseG1GC",
            "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication"))
        ports.set(listOf(8080))
        mainClassName.set(project.application.mainClassName)
    }
}

sonarqube {
    properties {
        // See https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner+for+Gradle#AnalyzingwithSonarQubeScannerforGradle-Configureanalysisproperties
        property("sonar.sourceEncoding", "UTF-8")
        val projectName = "kcd"
        property("sonar.projectName", projectName)
        property("sonar.projectKey", System.getenv()["SONAR_PROJECT_KEY"] ?: projectName)
        property("sonar.organization", System.getenv()["SONAR_ORGANIZATION"] ?: "triplem")
        property("sonar.projectVersion", project.version.toString())
        property("sonar.host.url", System.getenv()["SONAR_HOST_URL"] ?: "https://sonarcloud.io")
        property("sonar.login", System.getenv()["SONAR_LOGIN"] ?: "a1303c954ac40b5bcf728edc4d0fa810e618ec18")
        property("sonar.scm.provider", "git")
//        property("sonar.links.homepage", "https://jmeter.apache.org")
//        property("sonar.links.ci", "https://builds.apache.org/job/JMeter-trunk/")
//        property("sonar.links.scm", "https://jmeter.apache.org/svnindex.html")
//        property("sonar.links.issue", "https://jmeter.apache.org/issues.html")
        property("sonar.coverage.jacoco.xmlReportPaths", "$buildDir/reports/jacoco/test/jacoco.xml")
        property("sonar.kotlin.detekt.reportPaths", "build/reports/detekt/detekt.xml")
    }
}

detekt {
    toolVersion = "1.8.0"                                 // Version of the Detekt CLI that will be used. When unspecified the latest detekt version found will be used. Override to stay on the same version.
    input = files(                                        // The directories where detekt looks for source files. Defaults to `files("src/main/java", "src/main/kotlin")`.
        "src/main/kotlin"
    )
    parallel = false                                      // Builds the AST in parallel. Rules are always executed in parallel. Can lead to speedups in larger projects. `false` by default.
    //config = files("detekt-config.yml")                  // Define the detekt configuration(s) you want to use. Defaults to the default detekt configuration.
    buildUponDefaultConfig = false                        // Interpret config files as updates to the default config. `false` by default.
    ignoreFailures = true                                // If set to `true` the build does not fail when the maxIssues count was reached. Defaults to `false`.
}

tasks {
    "asciidoctor"(AsciidoctorTask::class) {
        sourceDir = file("src/docs")
        outputDir = file("build/docs")

        attributes(
            mapOf(
                "source-highlighter" to "rouge",
                "toc"                to "left",
                "toclevels"          to 2,
                "idprefix"           to "",
                "idseparator"        to "-"
            )
        )
    }

    "jacocoTestReport"(JacocoReport::class) {
        reports {
            xml.isEnabled = true
            xml.destination = File("$buildDir/reports/jacoco/test/jacoco.xml")
        }
    }

    "detekt"(io.gitlab.arturbosch.detekt.Detekt::class) {
        exclude("**/deprecated/**")
    }

    "sonarqube"(SonarQubeTask::class) {
        dependsOn("jacocoTestReport")
        dependsOn("detekt")
    }

}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
    jcenter()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(platform(kotlin("bom")))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    implementation("org.slf4j:jcl-over-slf4j:1.7.30")
    implementation("io.github.microutils:kotlin-logging:1.7.9")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    implementation(platform(ktor("bom:1.3.2")))
    implementation(ktor("server-cio"))
    implementation(ktor("server-core"))
    implementation(ktor("server-host-common"))
    implementation(ktor("auth"))
    implementation(ktor("auth-jwt"))
    implementation(ktor("serialization"))
    implementation(ktor("locations"))

    implementation("com.github.zensum:ktor-health-check:011a5a8")

    implementation("org.kodein.di:kodein-di-generic-jvm:$kodein_version")
    implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:$kodein_version")

    implementation("com.datastax.oss:java-driver-core:$datastax_version")
    implementation("com.datastax.oss:java-driver-query-builder:$datastax_version")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testImplementation(ktor("server-tests"))
    testImplementation("com.github.nosan:embedded-cassandra-junit5-test:3.0.3")
    testImplementation("org.junit.jupiter:junit-jupiter:$junit_version")
    testImplementation("io.mockk:mockk:1.10.0")

    testRuntimeOnly("co.elastic.logging:logback-ecs-encoder:0.4.0")
}
