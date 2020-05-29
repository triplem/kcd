import java.text.SimpleDateFormat
import java.util.*

group = "org.javafreedom.kcd"

val github_url: String by project
val github_org: String by project
val project_name: String by project

val github_project_url: String by extra("${github_url}/${github_org}/${project_name}")

val buildRevision = System.getenv()["revnumber"] ?: "DEV-Version"
val logback_version: String by project
val datastax_version: String by project
val kodein_version: String by project
val junit_version: String by project
val project_reports_dir: String by extra("$buildDir/reports")
val sonar_login: String? by extra(System.getenv()["SONAR_TOKEN"])

val ghToken = System.getenv()["GITHUB_TOKEN"] ?: ""

val revDate = System.getenv()["revdate"] ?: SimpleDateFormat("yyyy-MM-dd").format(Date())
val dockerTag = System.getenv()["revnumber"] ?: "latest"

//plugins {
//    kotlin("jvm")
//    kotlin("plugin.serialization") version "1.4.10"
//    application
//    integrationTest
//    detekt
//    id("com.bmuschko.docker-java-application") version "6.6.1"
//    id("org.owasp.dependencycheck") version "6.0.2"
//    id("maven-publish")
//}
//
//val sonarqube: Boolean = true
//
//group = "org.javafreedom.kcd"
//
//tasks.withType<KotlinCompile>().configureEach {
//    kotlinOptions.jvmTarget = "1.8"
//}
//
//application {
//    mainClassName = "io.ktor.server.cio.EngineMain"
//}
//
//docker {
//    javaApplication {
//        baseImage.set("openjdk:11-jdk-slim")
//        maintainer.set("KCD-Team 'kcd@somewhere.com'")
//        jvmArgs.set(listOf("-server", "-XX:+UnlockExperimentalVMOptions", "-XX:InitialRAMFraction=2",
//            "-XX:MinRAMFraction=2", "-XX:MaxRAMFraction=2", "-XX:+UseG1GC",
//            "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication"))
//        ports.set(listOf(8080))
//        mainClassName.set(project.application.mainClassName)
//        images.set(listOf("${project.group}/${project.name}:latest",
//            "ghcr.io/${github_org}/${project.name}:${dockerTag}"))
//    }
//}
//
//dependencyCheck {
//    failBuildOnCVSS = 3F
//    formats = listOf(Format.HTML, Format.JUNIT, Format.XML)
//    suppressionFile = "$projectDir/config/owasp/owasp-supression.xml"
//}
//
//tasks {
//
//    val sourcesJar by creating(Jar::class) {
//        archiveClassifier.set("sources")
//        from(sourceSets.main.get().allSource)
//    }
//}
//
//publishing {
//    repositories {
//        maven {
//            name = "GitHubPackages"
//            url = uri("https://maven.pkg.github.com/$github_org/$project_name")
//            credentials {
//                username = "i-dont-care"
//                password = System.getenv("GITHUB_TOKEN")
//            }
//        }
//    }
//    publications {
//        create<MavenPublication>("gpr") {
//            from(components["java"])
//            artifact(tasks["sourcesJar"])
//        }
//    }
//}
//
////dependencies {
////    implementation(platform(kotlin("bom")))
////    implementation(kotlin("stdlib-jdk8"))
////    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
////
////    implementation(project(":application-core"))
////    implementation(project(":primary-adapters"))
////    implementation(project(":secondary-adapters"))
////
////    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
////
////    implementation("org.slf4j:jcl-over-slf4j:1.7.30")
////    implementation("io.github.microutils:kotlin-logging:2.0.3")
////    implementation("ch.qos.logback:logback-classic:$logback_version")
////
////    implementation(platform(ktor("bom:1.4.1")))
////    implementation(ktor("server-cio"))
////    implementation(ktor("server-core"))
////    implementation(ktor("server-host-common"))
////    implementation(ktor("auth"))
////    implementation(ktor("auth-jwt"))
////    implementation(ktor("serialization"))
////    implementation(ktor("locations"))
////
////    implementation("com.github.zensum:ktor-health-check:011a5a8")
////
////    implementation("org.kodein.di:kodein-di-generic-jvm:$kodein_version")
////    implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:$kodein_version")
////
////    implementation("com.datastax.oss:java-driver-core:$datastax_version")
////    implementation("com.datastax.oss:java-driver-query-builder:$datastax_version")
////    implementation("io.netty:netty-handler:4.1.46.Final")
////    implementation("org.apache.tinkerpop:gremlin-core:3.4.8")
////    implementation("org.apache.tinkerpop:gremlin-driver:3.4.8")
////    implementation("org.apache.tinkerpop:tinkergraph-gremlin:3.4.8")
////
////    testImplementation(kotlin("test"))
////    testImplementation(kotlin("test-junit5"))
////    testImplementation(ktor("server-tests"))
////    testImplementation("com.github.nosan:embedded-cassandra-junit5-test:3.0.3")
////    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.23")
////    testImplementation("io.mockk:mockk:1.10.0")
////
////    testRuntimeOnly("co.elastic.logging:logback-ecs-encoder:0.4.0")
////    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit_version")
////    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit_version")
////}
