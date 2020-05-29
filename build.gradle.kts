plugins {
    id("org.jetbrains.dokka")

    id("org.javafreedom.verification.jacoco-consumer-conventions")
    id("org.javafreedom.verification.test-consumer-conventions")
    id("org.javafreedom.documentation.documentation-consumer-conventions")

    id("org.javafreedom.verification.sonarqube-conventions")
    id("org.javafreedom.aggregation-conventions")
}

allprojects {
    group = "org.javafreedom.gradle"
}

repositories {
    jcenter()
    maven { url = uri("https://jitpack.io") }
}

// this task generates all tasks for sub-projects itself, therefor it just needs
// to be applied on the root project, conventions are not working :-(
tasks.dokkaHtmlMultiModule.configure {
    outputDirectory.set(buildDir.resolve("dokka"))
}

dependencies {
    implementation(project(":ktor-app"))

    asciidoc(project(":architecture"))
    asciidoc(project(":articles"))
}
