@file:Suppress("ConvertLambdaToReference")

import io.guthix.oldscape.server.template.TemplateGenerator
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

plugins {
    idea
    application
    id("org.jetbrains.dokka")
    kotlin("jvm")
}

apply<TemplateGenerator>()

group = "io.guthix.oldscape"
version = "0.1.0-SNAPSHOT"
description = "An Oldschool Runescape Server Emulator"

application { mainClass.set("io.guthix.oldscape.server.OldScape") }

val kotlinLoggingVersion: String by extra("2.0.2")
val kotlinCoroutinesVersion: String by extra("1.3.2")
val classGraphVersion: String by extra("4.8.53")
val logbackVersion: String by extra("1.2.3")
val nettyVersion: String by extra("4.1.42.Final")
val jacksonVersion: String by extra("2.10.2")
val oldscapeCacheVersion: String by extra("0.1.0")
val kotlinVersion: String by extra(project.getKotlinPluginVersion()!!)

allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "org.jetbrains.dokka")

    group = rootProject.group
    version = rootProject.version

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

dependencies {
    project(":plugins").dependencyProject.subprojects.forEach { pluginProject ->
        if (pluginProject.buildFile.exists()) {
            runtimeOnly(pluginProject)
        }
    }
    api(group = "io.guthix.oldscape", name = "oldscape-cache", version = oldscapeCacheVersion)
    implementation(group = "org.jetbrains.kotlin", name = "kotlin-reflect", version = kotlinVersion)
    implementation(group = "org.jetbrains.kotlin", name = "kotlin-scripting-common", version = kotlinVersion)
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = kotlinCoroutinesVersion)
    implementation(group = "io.netty", name = "netty-all", version = nettyVersion)
    implementation(group = "io.github.classgraph", name = "classgraph", version = classGraphVersion)
    implementation(group = "ch.qos.logback", name = "logback-classic", version = logbackVersion)
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = jacksonVersion)
    implementation(
        group = "com.fasterxml.jackson.dataformat", name = "jackson-dataformat-yaml", version = jacksonVersion
    )
    implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-kotlin", version = jacksonVersion)
}