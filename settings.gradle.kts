pluginManagement {
    val kotlinVersion by extra("1.4.0")
    val dokkaVersion by extra(kotlinVersion)

    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
        id("org.jetbrains.dokka") version dokkaVersion
    }

    repositories {
        gradlePluginPortal()
        jcenter()
    }
}

rootProject.name = "oldscape-wiki"

include("parser")
include("downloader")