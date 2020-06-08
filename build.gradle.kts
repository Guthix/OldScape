@file:Suppress("ConvertLambdaToReference")

plugins {
    idea
    application
    kotlin("jvm") version "1.4-M2"
    id("com.github.hierynomus.license") version "0.15.0"
}

group = "io.guthix"
version = "0.1-SNAPSHOT"
description = "An Oldschool Runescape Cache EmulaTOR"

val licenseHeader by extra(file("AGPLv3.txt"))

application { mainClass.set("io.guthix.oldscape.server.OldScape") }

val kotlinLoggingVersion by extra("1.7.6")
val kotlinCoroutinesVersion by extra("1.3.2")
val classGraphVersion by extra("4.8.53")
val logbackVersion by extra("1.2.3")
val nettyVersion by extra("4.1.42.Final")
val jacksonVersion by extra("2.10.2")
val oldscapeCacheVersion by extra("1a531f49a8")
val jagexByteBufVersion by extra("555807fda4")
val kotlinVersion by extra("1.4-M2")

allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "com.github.hierynomus.license")

    repositories {
        mavenCentral()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://jitpack.io")
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
    }

    license {
        header = licenseHeader
        exclude("*\\main_file_cache.*")
        exclude("**/*.json")
        exclude("**/*.xml")
        exclude("**/*.yaml")
        include("**/*.kts")
        mapping("kts", "JAVADOC_STYLE")
    }
}

dependencies {
    project(":plugins").dependencyProject.subprojects.forEach { pluginProject ->
        if(pluginProject.buildFile.exists()) {
            runtimeOnly(pluginProject)
        }
    }
    api(project(":dimensions"))
    api(project(":blueprints"))
    api(group = "com.github.guthix", name = "oldscape-cache", version = oldscapeCacheVersion)
    implementation(group = "com.github.guthix", name = "jagex-byteBuf", version =  jagexByteBufVersion)
    implementation(group = "org.jetbrains.kotlin", name = "kotlin-reflect", version =  kotlinVersion)
    implementation(group = "org.jetbrains.kotlin", name = "kotlin-scripting-common", version =  kotlinVersion)
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version =  kotlinCoroutinesVersion)
    implementation(group = "io.netty", name = "netty-all", version =  nettyVersion)
    implementation(group = "io.github.classgraph", name = "classgraph", version =  classGraphVersion)
    implementation(group = "io.github.microutils", name = "kotlin-logging", version =  kotlinLoggingVersion)
    implementation(group = "ch.qos.logback", name = "logback-classic", version = logbackVersion)
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = jacksonVersion)
    implementation(group = "com.fasterxml.jackson.dataformat", name = "jackson-dataformat-yaml", version = jacksonVersion)
    implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-kotlin", version = jacksonVersion)
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
        kotlinOptions.freeCompilerArgs = listOf("-Xopt-in=kotlin.ExperimentalStdlibApi", "-XXLanguage:+InlineClasses")
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }
}