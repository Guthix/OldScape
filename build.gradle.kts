@file:Suppress("ConvertLambdaToReference")

import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

plugins {
    idea
    `maven-publish`
    kotlin("jvm")
    id("org.jetbrains.dokka")
}

group = "io.guthix"
version = "0.1-SNAPSHOT"
description = "A library for modifying OldScape caches"

val jagexCacheVersion: String by extra("d4fad90b09")
val kotlinLoggingVersion: String by extra("1.8.3")
val logbackVersion: String by extra("1.2.3")
val kotlinVersion: String by extra(project.getKotlinPluginVersion()!!)

allprojects {
    apply(plugin = "kotlin")

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
    }

    tasks {
        compileKotlin {
            kotlinOptions.jvmTarget = "11"
        }

        compileTestKotlin {
            kotlinOptions.jvmTarget = "11"
        }
    }
}

kotlin { explicitApi() }

dependencies {
    api(group = "com.github.guthix", name = "jagex-store-5", version = jagexCacheVersion)
    implementation(group = "io.github.microutils", name = "kotlin-logging", version = kotlinLoggingVersion)
    implementation(group = "ch.qos.logback", name = "logback-classic", version = logbackVersion)
}

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
            pom {
                url.set("https://github.com/guthix/OldScape-Cache")
                licenses {
                    license {
                        name.set("APACHE LICENSE, VERSION 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/guthix/OldScape-Cache.git")
                    developerConnection.set("scm:git:ssh://github.com/guthix/OldScape-Cache.git")
                }
            }
        }
    }
}