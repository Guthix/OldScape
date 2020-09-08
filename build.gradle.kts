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
version = "0.1"
description = "A library for modifying OldScape caches"

val repoUrl: String = "https://github.com/guthix/OldScape-Cache"
val gitSuffix: String = "github.com/guthix/OldScape-Cache.git"

val jagexStore5Version: String by extra("0.4.0")
val kotlinLoggingVersion: String by extra("1.8.3")
val logbackVersion: String by extra("1.2.3")
val kotlinVersion: String by extra(project.getKotlinPluginVersion()!!)

allprojects {
    apply(plugin = "kotlin")

    repositories {
        mavenCentral()
        jcenter()
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
    api(group = "io.guthix", name = "jagex-store-5", version = jagexStore5Version)
    implementation(group = "io.github.microutils", name = "kotlin-logging", version = kotlinLoggingVersion)
    implementation(group = "ch.qos.logback", name = "logback-classic", version = logbackVersion)
    dokkaHtmlPlugin(group = "org.jetbrains.dokka", name = "kotlin-as-java-plugin", version = kotlinVersion)
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
            url = URI("https://maven.pkg.github.com/guthix/OldScape-Cache")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
            pom {
                name.set("OldScape Cache")
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
                    url.set(
                        repoUrl
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