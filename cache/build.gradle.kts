@file:Suppress("ConvertLambdaToReference")

import io.guthix.oldscape.registerPublication

plugins {
    `maven-publish`
    signing
}

group = "io.guthix"
version = "0.1.0"
description = "A library for modifying OldScape caches"

val jagexStore5Version: String by extra("0.4.0")
val logbackVersion: String by extra("1.2.3")
val kotlinVersion: String by rootProject.extra

kotlin { explicitApi() }

java {
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    api(group = "io.guthix", name = "jagex-store-5", version = jagexStore5Version)
    implementation(group = "ch.qos.logback", name = "logback-classic", version = logbackVersion)
    dokkaHtmlPlugin(group = "org.jetbrains.dokka", name = "kotlin-as-java-plugin", version = kotlinVersion)
}

registerPublication(
    publicationName = "oldscapeCache",
    pomName = "oldscape-cache"
)