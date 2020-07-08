@file:Suppress("ConvertLambdaToReference")

plugins {
    idea
    kotlin("jvm")
    id("com.github.hierynomus.license")
}

group = "io.guthix"
version = "0.1-SNAPSHOT"

val licenseHeader: File by extra(file("../gradle/LICENSE_HEADER"))

val oldscapeCacheVersion: String by extra("1a531f49a8")
val kotlinpoetVersion: String by extra("1.6.0")

repositories {
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    maven("https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(group = "com.github.guthix", name = "oldscape-cache", version = oldscapeCacheVersion)
}

license {
    header = licenseHeader
    strictCheck = true
}