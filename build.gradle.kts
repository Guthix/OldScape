@file:Suppress("ConvertLambdaToReference")

import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import io.guthix.oldscape.server.cache.IdentifierGenerator

plugins {
    idea
    application
    `maven-publish`
    kotlin("jvm")
    id("org.jetbrains.dokka")
    id("com.github.hierynomus.license")
}

apply<IdentifierGenerator>()

group = "io.guthix"
version = "0.1-SNAPSHOT"
description = "An Oldschool Runescape Server Emulator"

val licenseHeader: File by extra(file("gradle/LICENSE_HEADER"))

application { mainClass.set("io.guthix.oldscape.server.OldScape") }

val kotlinLoggingVersion: String by extra("1.7.6")
val kotlinCoroutinesVersion: String by extra("1.3.2")
val classGraphVersion: String by extra("4.8.53")
val logbackVersion: String by extra("1.2.3")
val nettyVersion: String by extra("4.1.42.Final")
val jacksonVersion: String by extra("2.10.2")
val oldscapeCacheVersion: String by extra("1a531f49a8")
val jagexByteBufVersion: String by extra("555807fda4")
val kotlinVersion: String by extra(project.getKotlinPluginVersion()!!)

allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "com.github.hierynomus.license")
    apply(plugin = "maven-publish")
    apply(plugin = "org.jetbrains.dokka")

    repositories {
        mavenCentral()
        jcenter()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://jitpack.io")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
    }

    license {
        header = licenseHeader
        strictCheck = true
        exclude("*\\main_file_cache.*")
        exclude("**/*.json")
        exclude("**/*.xml")
        exclude("**/*.yaml")
        include("**/*.kt")
        include("**/*.kts")
        mapping("kt", "JAVADOC_STYLE")
        mapping("kts", "JAVADOC_STYLE")
    }

    tasks {
        dokka {
            outputFormat = "html"
            outputDirectory = "$buildDir/javadoc"
        }

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
    api(project(":dimensions"))
    api(project(":blueprints"))
    api(group = "com.github.guthix", name = "oldscape-cache", version = oldscapeCacheVersion)
    implementation(group = "com.github.guthix", name = "jagex-byteBuf", version = jagexByteBufVersion)
    implementation(group = "org.jetbrains.kotlin", name = "kotlin-reflect", version = kotlinVersion)
    implementation(group = "org.jetbrains.kotlin", name = "kotlin-scripting-common", version = kotlinVersion)
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = kotlinCoroutinesVersion)
    implementation(group = "io.netty", name = "netty-all", version = nettyVersion)
    implementation(group = "io.github.classgraph", name = "classgraph", version = classGraphVersion)
    implementation(group = "io.github.microutils", name = "kotlin-logging", version = kotlinLoggingVersion)
    implementation(group = "ch.qos.logback", name = "logback-classic", version = logbackVersion)
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = jacksonVersion)
    implementation(
        group = "com.fasterxml.jackson.dataformat", name = "jackson-dataformat-yaml", version = jacksonVersion
    )
    implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-kotlin", version = jacksonVersion)
}

license {
    exclude("**/LocId.kt")
    exclude("**/NpcId.kt")
    exclude("**/ObjId.kt")
}