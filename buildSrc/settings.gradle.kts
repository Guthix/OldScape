pluginManagement {
    val kotlinVersion by extra("1.4.0")
    val dokkaVersion by extra(kotlinVersion)

    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
        id("org.jetbrains.dokka") version dokkaVersion
    }

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}