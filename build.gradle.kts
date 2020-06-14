@file:Suppress("ConvertLambdaToReference")

import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

plugins {
    idea
    kotlin("jvm")
    id("org.jetbrains.dokka")
    id("com.github.hierynomus.license")
}

group = "io.guthix"
version = "0.1-SNAPSHOT"
description = "A library for interfacing with the Oldschool Runescape Wiki"

val licenseHeader: File by extra(file("LGPLv3.txt"))

val oldscapeServerVersion: String by extra("219c0b20eb")
val oldscapeCacheVersion: String by extra("6d3cc20a7f")
val kotlinCoroutinesVersion: String by extra("1.3.4")
val licensePluginVersion: String by extra("0.15.0")
val kotlinLoggingVersion: String by extra("1.7.6")
val logbackVersion: String by extra("1.2.3")
val ktorVersion: String by extra("1.3.1")
val jacksonVersion: String by extra("2.10.2")
val kotlinVersion: String by extra(project.getKotlinPluginVersion()!!)

allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "com.github.hierynomus.license")
    apply(plugin = "org.jetbrains.dokka")

    repositories {
        mavenCentral()
        jcenter()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation(group = "io.github.microutils", name = "kotlin-logging", version = kotlinLoggingVersion)
    }

    tasks {
        dokka {
            outputFormat = "html"
            outputDirectory = "$buildDir/javadoc"
        }

        compileKotlin {
            kotlinOptions.jvmTarget = "11"
        }

        compileTestKotlin {
            kotlinOptions.jvmTarget = "11"
        }
    }

    license {
        header = licenseHeader
        exclude("*\\main_file_cache.*")
        exclude("**/*.json")
        exclude("**/*.xml")
        exclude("**/*.yaml")
    }
}

gradle.buildFinished {
    buildDir.deleteRecursively()
}