@file:Suppress("ConvertLambdaToReference")

import io.guthix.oldscape.server.cache.CodeGenerator
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

plugins {
    idea
    application
    `maven-publish`
    kotlin("jvm")
    id("org.jetbrains.dokka")
}

apply<CodeGenerator>()

group = "io.guthix.oldscape"
version = "0.1-SNAPSHOT"
description = "An Oldschool Runescape Server Emulator"

application { mainClass.set("io.guthix.oldscape.server.OldScape") }

val kotlinLoggingVersion: String by extra("1.7.6")
val kotlinCoroutinesVersion: String by extra("1.3.2")
val classGraphVersion: String by extra("4.8.53")
val logbackVersion: String by extra("1.2.3")
val nettyVersion: String by extra("4.1.42.Final")
val jacksonVersion: String by extra("2.10.2")
val oldscapeCacheVersion: String by extra("b032d9a103")
val kotlinVersion: String by extra(project.getKotlinPluginVersion()!!)

allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "org.jetbrains.dokka")

    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
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

configure(allprojects.filter {
    it.name == rootProject.name || it.name == "equipment" || it.name == "combat" || it.name == "npc"
        || it.name == "obj" || it.name == "loc"
}) {
    val project = this
    apply(plugin = "maven-publish")
    publishing {
        publications {
            create<MavenPublication>("default") {
                artifactId = if(project.name == rootProject.name ) {
                    rootProject.name
                } else "${rootProject.name}-${project.name}"
                from(components["java"])
                pom {
                    url.set("https://github.com/guthix/OldScape-Server")
                    licenses {
                        license {
                            name.set("APACHE LICENSE, VERSION 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/guthix/OldScape-Server.git")
                        developerConnection.set("scm:git:ssh://github.com/guthix/OldScape-Server.git")
                    }
                }
            }
        }
    }
}

dependencies {
    project(":plugins").dependencyProject.subprojects.forEach { pluginProject ->
        if (pluginProject.buildFile.exists()) {
            runtimeOnly(pluginProject)
        }
    }
    api(group = "com.github.guthix", name = "oldscape-cache", version = oldscapeCacheVersion)
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