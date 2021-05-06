import org.gradle.kotlin.dsl.support.listFilesOrdered

rootProject.name = "kcd"

pluginManagement {
    val kotlinVersion: String by settings
    plugins {
        kotlin("plugin.serialization") version "1.4.32"
    }

    repositories {
        gradlePluginPortal()
        jcenter()
    }
}

fun includeProject(dir: File) {
    include(dir.name)
    val prj = project(":${dir.name}")
    prj.projectDir = dir
    prj.buildFileName = "${dir.name}.gradle.kts"
    require(prj.projectDir.isDirectory) { "Project '${prj.path} must have a ${prj.projectDir} directory" }
    require(prj.buildFile.isFile) { "Project '${prj.path} must have a ${prj.buildFile} build script" }
}

fun includeProjectsInDir(dirName: String) {
    file(dirName).listFilesOrdered { it.isDirectory }
        .forEach { dir ->
            includeProject(dir)
        }
}

includeProjectsInDir("modules")
includeProjectsInDir("documentation")