pluginManagement {
    val kotlinVersion by extra("1.4-M3")
    val dokkaVersion by extra("0.10.0")
    val licensePluginVersion by extra("0.15.0")

    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
        id("org.jetbrains.dokka") version dokkaVersion
        id("com.github.hierynomus.license") version licensePluginVersion
    }

    repositories {
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }
}