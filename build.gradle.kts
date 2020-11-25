@file:Suppress("ConvertLambdaToReference")

import io.guthix.oldscape.server.template.TemplateGenerator
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

plugins {
    idea
    application
    id("org.jetbrains.dokka")
    kotlin("jvm")
}

group = "io.guthix.oldscape"
description = "An Oldschool Runescape Emulation"

val kotlinLoggingVersion: String by extra("2.0.2")
val kotlinVersion: String by extra("1.4.10")

allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "org.jetbrains.dokka")

    repositories {
        jcenter()
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