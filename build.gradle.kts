@file:Suppress("ConvertLambdaToReference")

plugins {
    idea
    `maven-publish`
    kotlin("jvm")
    id("org.jetbrains.dokka")
    id("com.github.hierynomus.license")
}

group = "io.guthix"
version = "0.1-SNAPSHOT"
description = "A library for modifying OldScape caches"

val licenseHeader: File by extra(file("LGPLv3.txt"))

val jagexByteBufVersion: String by extra("555807fda4")
val jagexCacheVersion: String by extra("b95030a6f6")
val kotlinLoggingVersion: String by extra("1.7.6")
val kotlinVersion: String by extra("1.3.70")
val licensePluginVersion: String by extra("0.15.0")
val logbackVersion: String by extra("1.2.3")

allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "com.github.hierynomus.license")

    repositories {
        mavenCentral()
        jcenter()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://jitpack.io")
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
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
    }
}

kotlin { explicitApi() }

dependencies {
    api(group = "com.github.guthix", name = "Jagex-Store-5", version = jagexCacheVersion)
    implementation(group = "com.github.guthix", name = "Jagex-ByteBuf", version = jagexByteBufVersion)
    implementation(group = "io.github.microutils", name = "kotlin-logging", version = kotlinLoggingVersion)
    implementation(group = "ch.qos.logback", name = "logback-classic", version = logbackVersion)
}

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
                url.set("https://github.com/guthix/OldScape-Cache")
                licenses {
                    license {
                        name.set("GNU Lesser General Public License v3.0")
                        url.set("https://www.gnu.org/licenses/lgpl-3.0.txt")
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