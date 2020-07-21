@file:Suppress("ConvertLambdaToReference")

plugins {
    idea
    kotlin("jvm")
}

group = "io.guthix"
version = "0.1-SNAPSHOT"

val oldscapeCacheVersion: String by extra("0891d18096")
val kotlinpoetVersion: String by extra("1.6.0")
val jacksonVersion: String by extra("2.10.2")

repositories {
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    maven("https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(group = "com.github.guthix", name = "oldscape-cache", version = oldscapeCacheVersion)
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = jacksonVersion)
    implementation(
        group = "com.fasterxml.jackson.dataformat", name = "jackson-dataformat-yaml", version = jacksonVersion
    )
    implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-kotlin", version = jacksonVersion)
}