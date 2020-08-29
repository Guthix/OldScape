pluginManagement {
    val kotlinVersion by extra("1.4.0")
    val dokkaVersion by extra("1.4.0-rc")

    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
        id("org.jetbrains.dokka") version dokkaVersion
    }

    repositories {
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }
}

rootProject.name = "oldscape-cache"

include("formats")