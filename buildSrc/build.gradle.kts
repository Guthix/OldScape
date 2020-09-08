@file:Suppress("ConvertLambdaToReference")

import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

plugins {
    idea
    kotlin("jvm")
}

group = "io.guthix"
version = "0.1"

val oldscapeCacheVersion: String by extra("0.1.0")
val kotlinpoetVersion: String by extra("1.6.0")
val jacksonVersion: String by extra("2.10.2")
val kotlinVersion: String by extra(project.getKotlinPluginVersion()!!)

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation(group = "org.jetbrains.kotlin", name = "kotlin-reflect", version = kotlinVersion)
    implementation(group = "io.guthix.oldscape", name = "oldscape-cache", version = oldscapeCacheVersion)
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = jacksonVersion)
    implementation(
        group = "com.fasterxml.jackson.dataformat", name = "jackson-dataformat-yaml", version = jacksonVersion
    )
    implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-kotlin", version = jacksonVersion)
}