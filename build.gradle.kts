import org.asciidoctor.gradle.AsciidoctorTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.owasp.dependencycheck.reporting.ReportGenerator.Format
import org.sonarqube.gradle.SonarQubeTask
import java.text.SimpleDateFormat
import java.util.Date

val project_name: String by project
val logback_version: String by project
val datastax_version: String by project
val kodein_version: String by project
val junit_version: String by project
val github_url: String by project
val github_org: String by project

val github_project_url = "$github_url/$github_org/$project_name"
val project_reports_dir = "$buildDir/reports"

val ghToken = System.getenv()["GITHUB_TOKEN"] ?: ""

val revDate = System.getenv()["revdate"] ?: SimpleDateFormat("yyyy-MM-dd").format(Date())
val revNumber = System.getenv()["revnumber"] ?: "KCD-Team"

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
    id("org.owasp.dependencycheck") version "5.3.2.1"
    id("maven-publish")
}

group = "org.javafreedom.kcd"

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "io.ktor.server.cio.EngineMain"
}

docker {
    javaApplication {
        baseImage.set("openjdk:11-jdk-slim")
        maintainer.set("KCD-Team 'kcd@somewhere.com'")
        jvmArgs.set(listOf("-server", "-XX:+UnlockExperimentalVMOptions", "-XX:InitialRAMFraction=2",
            "-XX:MinRAMFraction=2", "-XX:MaxRAMFraction=2", "-XX:+UseG1GC",
            "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication"))
        ports.set(listOf(8080))
        mainClassName.set(project.application.mainClassName)
    }
}

sonarqube {
    properties {
        // See https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner+for+Gradle#AnalyzingwithSonarQubeScannerforGradle-Configureanalysisproperties
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.projectName", "$project_name")
        property("sonar.projectKey", System.getenv()["SONAR_PROJECT_KEY"] ?: "$project_name")
        property("sonar.organization", System.getenv()["SONAR_ORGANIZATION"] ?: "$github_org")
        property("sonar.projectVersion", project.version.toString())
        property("sonar.host.url", System.getenv()["SONAR_HOST_URL"] ?: "https://sonarcloud.io")
        property("sonar.login", System.getenv()["SONAR_TOKEN"] ?: "" )
        property("sonar.scm.provider", "git")
        property("sonar.links.homepage", "$github_project_url")
        property("sonar.links.ci", "$github_project_url/actions")
        property("sonar.links.scm", "$github_project_url")
        property("sonar.links.issue", "$github_project_url/issues")
        property("sonar.coverage.jacoco.xmlReportPaths", "$project_reports_dir/jacoco/test/jacoco.xml")
        property("sonar.kotlin.detekt.reportPaths", "$project_reports_dir/detekt/detekt.xml")
    }
}

detekt {
    toolVersion = "1.8.0"
    input = files(
        "src/main/kotlin"
    )
    parallel = false
    buildUponDefaultConfig = false
    ignoreFailures = true
}

dependencyCheck {
    failBuildOnCVSS = 3F
    formats = listOf(Format.HTML, Format.JUNIT, Format.XML)
    suppressionFile = "$projectDir/config/owasp/owasp-supression.xml"
}

tasks {
    "asciidoctor"(AsciidoctorTask::class) {
        sourceDir = file("src/docs")
        outputDir = file("$buildDir/docs")

        attributes(
            mapOf(
                "source-highlighter" to "rouge",
                "toc"                to "left",
                "toclevels"          to 2,
                "idprefix"           to "",
                "idseparator"        to "-",
                "revnumber"          to "",
                "revdate"            to ""
            )
        )
    }

    "jacocoTestReport"(JacocoReport::class) {
        reports {
            xml.isEnabled = true
            xml.destination = File("$project_reports_dir/jacoco/test/jacoco.xml")
        }
    }

    "detekt"(io.gitlab.arturbosch.detekt.Detekt::class) {
        exclude("**/deprecated/**")
    }

    "sonarqube"(SonarQubeTask::class) {
        dependsOn("detekt")
    }

    val sourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.register("sonarqubeWithDependencies") {
    dependsOn("jacocoTestReport")
    dependsOn("detekt")
    dependsOn("sonarqube")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/$github_org/$project_name")
            credentials {
                username = "i-dont-care"
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("gpr") {
            from(components["java"])
            artifact(tasks["sourcesJar"])
        }
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
    implementation("io.netty:netty-handler:4.1.46.Final")
    implementation("org.apache.tinkerpop:gremlin-core:3.4.8")
    implementation("org.apache.tinkerpop:gremlin-driver:3.4.8")
    implementation("org.apache.tinkerpop:tinkergraph-gremlin:3.4.8")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testImplementation(ktor("server-tests"))
    testImplementation("com.github.nosan:embedded-cassandra-junit5-test:3.0.3")
    testImplementation("org.junit.jupiter:junit-jupiter:$junit_version")
    testImplementation("io.mockk:mockk:1.10.0")

    testRuntimeOnly("co.elastic.logging:logback-ecs-encoder:0.4.0")
}
