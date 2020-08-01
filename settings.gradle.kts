import groovy.util.FileNameFinder
import java.nio.file.Path
import java.nio.file.Paths

pluginManagement {
    val kotlinVersion by extra("1.4.0-rc")
    val dokkaVersion by extra("0.10.0")

    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
        id("org.jetbrains.dokka") version dokkaVersion
    }

    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
    }
}

rootProject.name = "oldscape-server"

include("dimensions")
includeModules("plugins")

fun includeModules(module: String) {
    val pluginRootDir: Path = rootProject.projectDir.toPath().resolve(module)
    if (pluginRootDir.toFile().exists()) {
        val gradleBuildFiles = FileNameFinder().getFileNames("$pluginRootDir", "**/*.gradle.kts")
        gradleBuildFiles.forEach { filename ->
            val buildFilePath = Paths.get(filename)
            val moduleDir = buildFilePath.parent
            val relativePath = pluginRootDir.relativize(moduleDir)
            val pluginName = "$relativePath".replace(File.separator, ":")
            include(":$module:$pluginName")
        }
    }
}