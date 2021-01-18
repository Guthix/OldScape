@file:Suppress("ConvertLambdaToReference")

import java.net.URI

plugins {
    `maven-publish`
    signing
}

group = "io.guthix.oldscape"
version = "0.1.0"
description = "A library for modifying OldScape caches"

val repoUrl: String = "https://github.com/guthix/OldScape-Cache"
val gitSuffix: String = "github.com/guthix/OldScape-Cache.git"

val jagexStore5Version: String by extra("0.4.0")
val logbackVersion: String by extra("1.2.3")
val kotlinVersion: String by rootProject.extra

kotlin { explicitApi() }

dependencies {
    api(group = "io.guthix", name = "jagex-store-5", version = jagexStore5Version)
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