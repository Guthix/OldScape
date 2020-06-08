import java.nio.file.Paths
import java.nio.file.Path
import groovy.util.FileNameFinder

pluginManagement {
    val kotlinVersion by extra("1.4-M2")
    val licensePluginVersion by extra("0.15.0")

    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
        id("com.github.hierynomus.license") version licensePluginVersion
    }

    repositories {
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }
}

rootProject.name = "oldscape-server"

include("dimensions")
include("blueprints")
includeModules("plugins")

fun includeModules(module: String) {
    val pluginRootDir: Path = rootProject.projectDir.toPath().resolve(module)
    if (pluginRootDir.toFile().exists()) {
        val gradleBuildFiles = FileNameFinder().getFileNames(pluginRootDir.toString(), "**/*.gradle.kts")
        gradleBuildFiles.forEach { filename ->
            val buildFilePath = Paths.get(filename)
            val moduleDir = buildFilePath.parent
            val relativePath = pluginRootDir.relativize(moduleDir)
            val pluginName = relativePath.toString().replace(File.separator, ":")
            include(":$module:$pluginName")
        }
    }
}