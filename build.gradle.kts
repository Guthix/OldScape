@file:Suppress("ConvertLambdaToReference")

import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

plugins {
    idea
    `maven-publish`
    kotlin("jvm")
    id("org.jetbrains.dokka")
    id("com.github.hierynomus.license")
}

group = "io.guthix"
version = "0.1-SNAPSHOT"
description = "A library for interfacing with the Oldschool Runescape Wiki"

val licenseHeader: File by extra(file("LGPLv3.txt"))

val oldscapeServerVersion: String by extra("3b4c3bdac5")
val oldscapeCacheVersion: String by extra("6d3cc20a7f")
val kotlinCoroutinesVersion by extra("1.3.4")
val licensePluginVersion by extra("0.15.0")
val kotlinLoggingVersion by extra("1.7.6")
val logbackVersion by extra("1.2.3")
val ktorVersion by extra("1.3.1")
val jacksonVersion by extra("2.10.2")
val kotlinVersion: String by extra(project.getKotlinPluginVersion()!!)

allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "com.github.hierynomus.license")

    repositories {
        mavenCentral()
        jcenter()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation(group = "io.github.microutils", name = "kotlin-logging", version = kotlinLoggingVersion)
    }

    tasks {
        compileKotlin {
            kotlinOptions.jvmTarget = "11"
        }

        compileTestKotlin {
            kotlinOptions.jvmTarget = "11"
        }
    }

    license {
        header = licenseHeader
        exclude("*\\main_file_cache.*")
        exclude("**/*.json")
        exclude("**/*.xml")
        exclude("**/*.yaml")
    }
}

dependencies {
    implementation(group = "org.jetbrains.kotlin", name = "kotlin-reflect", version = kotlinVersion)
}

kotlin { explicitApi() }

tasks.dokka {
    outputFormat = "html"
    outputDirectory = "$buildDir/javadoc"
}

val dokkaJar: Jar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    classifier = "javadoc"
    from(tasks.dokka)
}

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
            artifact(dokkaJar)
            pom {
                url.set("https://github.com/guthix/OldScape-Wiki")
                licenses {
                    license {
                        name.set("GNU Lesser General Public License v3.0")
                        url.set("https://www.gnu.org/licenses/lgpl-3.0.txt")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/guthix/OldScape-Wiki.git")
                    developerConnection.set("scm:git:ssh://github.com/guthix/OldScape-Wiki.git")
                }
            }
        }
    }
}