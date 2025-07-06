@file:Suppress("UnstableApiUsage")

import java.nio.file.Path

rootProject.name = "oldscape"

dependencyResolutionManagement {
    versionCatalogs {
        create("deps") {
            version("jdk", "11")
            version("kotlin", "2.2.0")
            version("json", "1.9.0")
            version("kaml", "0.35.3")
            version("coroutines", "1.5.2")
            version("netty", "4.1.66.Final")
            version("exposed", "0.34.1")
            version("postgres", "42.2.18")
            version("jagex-store-5", "0.4.0")
            version("jagex-bytebuf-ext", "0.2.0")
            version("classgraph", "4.8.53")
            version("kotlin-logging", "2.0.11")
            version("logback", "1.2.3")
            version("kotest", "5.0.0.M1")
            version("ktor",  "1.4.0")


            library("kotlin-reflect", "org.jetbrains.kotlin", "kotlin-reflect").versionRef("kotlin")
            library("kotlin-serialization", "org.jetbrains.kotlinx", "kotlinx-serialization-core")
                .versionRef("kotlin")
            library("kotlin-serialization-json", "org.jetbrains.kotlinx", "kotlinx-serialization-json-jvm")
                .versionRef("json")
            library("kotlin-serialization-yaml", "com.charleskorn.kaml", "kaml").versionRef("kaml")
            library("kotlin-coroutines", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").versionRef("coroutines")
            library("kotlin-scripting", "org.jetbrains.kotlin", "kotlin-scripting-common").versionRef("kotlin")
            library("ktor-server-core", "io.ktor", "ktor-server-core").versionRef("ktor")
            library("ktor-client-apache", "io.ktor", "ktor-client-apache").versionRef("ktor")
            library("netty-all", "io.netty", "netty-all").versionRef("netty")
            library("exposed-core", "org.jetbrains.exposed", "exposed-core").versionRef("exposed")
            library("exposed-jdbc", "org.jetbrains.exposed", "exposed-jdbc").versionRef("exposed")
            library("postgresql", "org.postgresql", "postgresql").versionRef("postgres")
            library("jagex-bytebuf-ext", "io.guthix", "jagex-bytebuf-extensions").versionRef("jagex-bytebuf-ext")
            library("jagex-js5", "io.guthix", "jagex-store-5").versionRef("jagex-store-5")
            library("classgraph", "io.github.classgraph", "classgraph").versionRef("classgraph")
            library("kotlin-logging", "io.github.microutils", "kotlin-logging-jvm").versionRef("kotlin-logging")
            library("logback", "ch.qos.logback", "logback-classic").versionRef("logback")
            library("kotest-junit", "io.kotest", "kotest-runner-junit5-jvm").versionRef("kotest")
            library("kotest-assert", "io.kotest", "kotest-assertions-core-jvm").versionRef("kotest")
            library("kotest-property", "io.kotest", "kotest-property").versionRef("kotest")
        }
    }
}

include("dim")

include("cache")
include("cache:names")

include("server")
include("server:wiki")
include("server:world")
includeModules("server:world:plugins")

include("wiki:parser")
include("wiki:downloader")


fun includeModules(module: String) {
    val pluginRelativePath = module.replace(":", "/")
    val pluginRootDir: Path = rootProject.projectDir.toPath().resolve(pluginRelativePath)
    if (pluginRootDir.toFile().exists()) {
        val gradleBuildFiles = fileTree("$pluginRootDir").files.filter { it.name.endsWith(".gradle.kts") }
        gradleBuildFiles.forEach { file ->
            val buildFilePath = file.toPath()
            val moduleDir = buildFilePath.parent
            val relativePath = pluginRootDir.relativize(moduleDir)
            val pluginName = "$relativePath".replace(File.separator, ":")
            include(":$module:$pluginName")
        }
    }
}