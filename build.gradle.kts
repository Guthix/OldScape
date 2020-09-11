@file:Suppress("ConvertLambdaToReference")

import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import java.net.URI

plugins {
    idea
    `maven-publish`
    signing
    id("org.jetbrains.dokka")
    kotlin("jvm")
}

group = "io.guthix.oldscape"
version = "0.1.0"
description = "A library for dumping the Oldschool Runescape Wiki"

val repoUrl: String = "https://github.com/guthix/OldScape-Wiki"
val gitSuffix: String = "github.com/guthix/OldScape-Wiki.git"

val oldscapeCacheVersion: String by extra("0.1.0")
val kotlinCoroutinesVersion: String by extra("1.3.9")
val kotlinLoggingVersion: String by extra("1.7.6")
val ktorVersion: String by extra("1.4.0")
val kotlinVersion: String by extra(project.getKotlinPluginVersion()!!)

allprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "kotlin")

    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(group = "io.github.microutils", name = "kotlin-logging", version = kotlinLoggingVersion)
    }

    kotlin { explicitApi() }

    tasks {
        compileKotlin {
            kotlinOptions.jvmTarget = "11"
        }

        compileTestKotlin {
            kotlinOptions.jvmTarget = "11"
        }
    }

    java {
        withJavadocJar()
        withSourcesJar()
    }

    publishing {
        repositories {
            maven {
                name = "MavenCentral"
                url = URI("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = System.getenv("OSSRH_USERNAME")
                    password = System.getenv("OSSRH_PASSWORD")
                }
            }
            maven {
                name = "GitHubPackages"
                url = URI("https://maven.pkg.github.com/guthix/OldScape-Wiki")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
        publications {
            create<MavenPublication>("default") {
                from(components["java"])
                artifactId = if(project.name == rootProject.name ) {
                    rootProject.name
                } else "${rootProject.name}-${project.name}"
                pom {
                    name.set("OldScape Wiki")
                    description.set(rootProject.description)
                    url.set(repoUrl)
                    licenses {
                        license {
                            name.set("APACHE LICENSE, VERSION 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://$gitSuffix")
                        developerConnection.set("scm:git:ssh://$gitSuffix")
                        url.set(repoUrl
                        )
                    }
                    developers {
                        developer {
                            id.set("bart")
                            name.set("Bart van Helvert")
                        }
                    }
                }
            }
        }
    }

    signing {
        useInMemoryPgpKeys(System.getenv("SIGNING_KEY"), System.getenv("SIGNING_PASSWORD"))
        sign(publishing.publications["default"])
    }
}

gradle.buildFinished {
    buildDir.deleteRecursively()
}