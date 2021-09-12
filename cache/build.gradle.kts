@file:Suppress("ConvertLambdaToReference")

import io.guthix.oldscape.registerPublication

plugins {
    `maven-publish`
    signing
}

val jagexStore5Version: String by extra("0.4.0")
val logbackVersion: String by extra("1.2.3")
val kotlinVersion: String by rootProject.extra

kotlin { explicitApi() }

dependencies {
    api(group = "io.guthix", name = "jagex-store-5", version = jagexStore5Version)
    api(project(":cache:names"))
    implementation(group = "ch.qos.logback", name = "logback-classic", version = logbackVersion)
    dokkaHtmlPlugin(group = "org.jetbrains.dokka", name = "kotlin-as-java-plugin", version = kotlinVersion)
}

registerPublication(name = "oldscape-cache", description = "A library for modifying OldScape caches")