@file:Suppress("ConvertLambdaToReference")

plugins {
    id("org.jetbrains.dokka")
    kotlin("jvm")
    id("com.github.gmazzo.buildconfig") version "2.0.2"
}

val kotlinLoggingVersion: String by extra("2.0.2")
val kCoroutinesVersion: String by extra("1.5.2")
val kotlinVersion: String by extra("1.5.30")
val logbackVersion: String by extra("1.2.3")

buildConfig {
    packageName("io.guthix.oldscape")
    buildConfigField(
        type ="java.nio.file.Path",
        name = "CACHE_PATH",
        value = "Path.of(\"${project(":cache").projectDir.resolve("src/main/resources")}\")"
            .replace("\\", "/")
    )
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.dokka")

    group = "io.guthix"
    version = "0.1.0"

    repositories {
        mavenCentral()
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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

subprojects {
    dependencies {
        implementation(rootProject)
        implementation(group = "io.github.microutils", name = "kotlin-logging", version = kotlinLoggingVersion)
    }
}