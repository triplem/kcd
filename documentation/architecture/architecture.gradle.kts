import org.asciidoctor.gradle.jvm.AsciidoctorTask

plugins {
    id("org.javafreedom.documentation.asciidoc-producer-conventions")
}

tasks.withType(AsciidoctorTask::class).configureEach {
    asciidoctorj {

        modules {
            diagram.use()

        }
    }

    attributes(
        mapOf(
            "source-highlighter" to "rouge",
            "doctype" to "book",
            "plantUmlDir" to "src/docs/asciidoc/"
//            "revnumber"          to "$project.revNumber",
//            "revdate"            to "$project.revDate"
        )
    )
}
