pluginManagement {
    val kotlinVersion by extra("1.4.0")
    val dokkaVersion by extra("1.4.0-rc")

    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
        id("org.jetbrains.dokka") version dokkaVersion
    }

    repositories {
        mavenCentral()
        gradlePluginPortal()
        jcenter()
    }
}