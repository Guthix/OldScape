@file:Suppress("ConvertLambdaToReference")

import io.guthix.oldscape.server.template.TemplateGenerator
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

plugins {
    idea
    application
    id("org.jetbrains.dokka")
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "io.guthix.oldscape"
version = "0.1.0-SNAPSHOT"
description = "An Oldschool Runescape Emulation"

val kotlinLoggingVersion: String by extra("2.0.2")
val kCoroutinesVersion: String by extra("1.3.2")
val classGraphVersion: String by extra("4.8.53")
val logbackVersion: String by extra("1.2.3")
val nettyVersion: String by extra("4.1.42.Final")
val ktSerVersion: String by extra("1.0.1")
val kamlVersion: String by extra("0.26.0")
val oldscapeCacheVersion: String by extra("0.1.0")
val kotlinVersion: String by extra("1.4.10")

allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "org.jetbrains.dokka")

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(group = "io.github.microutils", name = "kotlin-logging", version = kotlinLoggingVersion)
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks {
        compileKotlin {
            kotlinOptions.jvmTarget = "11"
            kotlinOptions.freeCompilerArgs = listOf(
                "-Xopt-in=kotlin.ExperimentalStdlibApi", "-XXLanguage:+InlineClasses"
            )
        }
        compileTestKotlin {
            kotlinOptions.jvmTarget = "11"
        }
    }
}