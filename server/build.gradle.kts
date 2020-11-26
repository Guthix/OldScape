@file:Suppress("ConvertLambdaToReference")

import io.guthix.oldscape.server.template.TemplateGenerator

plugins {
    idea
    application
    id("org.jetbrains.dokka")
    kotlin("jvm")
    kotlin("plugin.serialization")
}

apply<TemplateGenerator>()

group = "io.guthix.oldscape"
version = "0.1.0-SNAPSHOT"
description = "An Oldschool Runescape Server Emulator"

application { mainClass.set("io.guthix.oldscape.server.OldScape") }

val kCoroutinesVersion: String by rootProject.extra
val classGraphVersion: String by extra("4.8.53")
val logbackVersion: String by extra("1.2.3")
val nettyVersion: String by extra("4.1.42.Final")
val ktSerVersion: String by extra("1.0.1")
val kamlVersion: String by extra("0.26.0")
val exposedVersion: String by extra("0.28.1")
val postgresVersion: String by extra("42.2.18")
val kotlinVersion: String by rootProject.extra

allprojects {
    apply(plugin = "kotlinx-serialization")

    group = project(":server").group
    version = project(":server").version
}

dependencies {
    project(":server:plugins").dependencyProject.subprojects.forEach { pluginProject ->
        if (pluginProject.buildFile.exists()) {
            runtimeOnly(pluginProject)
        }
    }
    api(project(":cache"))
    api(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version = ktSerVersion)
    api(group = "org.jetbrains.kotlin", name = "kotlin-reflect", version = kotlinVersion)
    implementation(group = "org.jetbrains.kotlin", name = "kotlin-scripting-common", version = kotlinVersion)
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = kCoroutinesVersion)
    implementation(group = "io.netty", name = "netty-all", version = nettyVersion)
    implementation(group = "io.github.classgraph", name = "classgraph", version = classGraphVersion)
    implementation(group = "ch.qos.logback", name = "logback-classic", version = logbackVersion)
    implementation(group = "com.charleskorn.kaml", name = "kaml", version = kamlVersion)
    implementation(group = "org.jetbrains.exposed", name = "exposed-core", version = exposedVersion)
    implementation(group = "org.jetbrains.exposed", name = "exposed-jdbc", version = exposedVersion)
    implementation(group = "org.postgresql", name = "postgresql", version = postgresVersion)
}