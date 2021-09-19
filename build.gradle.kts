@file:Suppress("ConvertLambdaToReference")

plugins {
    kotlin("jvm") version "1.5.30"
    id("com.github.gmazzo.buildconfig") version "2.0.2"
}

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
        implementation(rootProject.deps.kotlin.logging)
    }
}