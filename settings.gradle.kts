@file:Suppress("UnstableApiUsage")

import groovy.util.FileNameFinder
import java.nio.file.Path
import java.nio.file.Paths

enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "oldscape"

dependencyResolutionManagement {
    versionCatalogs {
        create("deps") {
            version("jdk", "11")
            version("kotlin", "1.5.30")
            version("serialization", "1.2.2")
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


            alias("kotlin-reflect").to("org.jetbrains.kotlin", "kotlin-reflect").versionRef("kotlin")
            alias("kotlin-serialization").to("org.jetbrains.kotlinx", "kotlinx-serialization-core")
                .versionRef("serialization")
            alias("kotlin-serialization-json").to("org.jetbrains.kotlinx", "kotlinx-serialization-json")
                .versionRef("serialization")
            alias("kotlin-serialization-yaml").to("com.charleskorn.kaml", "kaml").versionRef("kaml")
            alias("kotlin-coroutines").to("org.jetbrains.kotlinx", "kotlinx-coroutines-core").versionRef("coroutines")
            alias("kotlin-scripting").to("org.jetbrains.kotlin", "kotlin-scripting-common").versionRef("kotlin")
            alias("ktor-server-core").to("io.ktor", "ktor-server-core").versionRef("ktor")
            alias("ktor-client-apache").to("io.ktor", "ktor-client-apache").versionRef("ktor")
            alias("netty-all").to("io.netty", "netty-all").versionRef("netty")
            alias("exposed-core").to("org.jetbrains.exposed", "exposed-core").versionRef("exposed")
            alias("exposed-jdbc").to("org.jetbrains.exposed", "exposed-jdbc").versionRef("exposed")
            alias("postgresql").to("org.postgresql", "postgresql").versionRef("postgres")
            alias("jagex-bytebuf-ext").to("io.guthix", "jagex-bytebuf-extensions").versionRef("jagex-bytebuf-ext")
            alias("jagex-js5").to("io.guthix", "jagex-store-5").versionRef("jagex-store-5")
            alias("classgraph").to("io.github.classgraph", "classgraph").versionRef("classgraph")
            alias("kotlin-logging").to("io.github.microutils", "kotlin-logging-jvm").versionRef("kotlin-logging")
            alias("logback").to("ch.qos.logback", "logback-classic").versionRef("logback")
            alias("kotest-junit").to("io.kotest", "kotest-runner-junit5-jvm").versionRef("kotest")
            alias("kotest-assert").to("io.kotest", "kotest-assertions-core-jvm").versionRef("kotest")
            alias("kotest-property").to("io.kotest", "kotest-property").versionRef("kotest")
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
        val gradleBuildFiles = FileNameFinder().getFileNames("$pluginRootDir", "**/*.gradle.kts")
        gradleBuildFiles.forEach { filename ->
            val buildFilePath = Paths.get(filename)
            val moduleDir = buildFilePath.parent
            val relativePath = pluginRootDir.relativize(moduleDir)
            val pluginName = "$relativePath".replace(File.separator, ":")
            include(":$module:$pluginName")
        }
    }
}