import org.asciidoctor.gradle.jvm.AsciidoctorTask

plugins {
    id("org.javafreedom.documentation.asciidoc-producer-conventions")
}

tasks.withType(AsciidoctorTask::class).configureEach {
    attributes(
        mapOf(
            "source-highlighter" to "rouge",
            "doctype" to "book"
//            "revnumber"          to "$project.revNumber",
//            "revdate"            to "$project.revDate"
        )
    )
}
