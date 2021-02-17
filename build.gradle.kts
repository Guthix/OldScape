@file:Suppress("ConvertLambdaToReference")

plugins {
    idea
    id("org.jetbrains.dokka")
    kotlin("jvm")
    id("com.github.gmazzo.buildconfig") version "2.0.2"
}

group = "io.guthix.oldscape"
description = "An Oldschool Runescape Emulation"

val kotlinLoggingVersion: String by extra("2.0.2")
val kCoroutinesVersion: String by extra("1.3.2")
val kotlinVersion: String by extra("1.4.10")
val logbackVersion: String by extra("1.2.3")

buildConfig {
    packageName("io.guthix.oldscape")
    buildConfigField(
        type ="java.nio.file.Path",
        name = "CACHE_PATH",
        value = "Path.of(\"${project(":cache").projectDir}\\src\\main\\resources\\cache\")"
            .replace("\\", "\\\\")
    )
}

allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "org.jetbrains.dokka")

    repositories {
        jcenter()
        mavenCentral()
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

subprojects {
    dependencies {
        implementation(rootProject)
        implementation(group = "io.github.microutils", name = "kotlin-logging", version = kotlinLoggingVersion)
    }
}