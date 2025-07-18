import de.undercouch.gradle.tasks.download.Download

plugins {
    kotlin("jvm") version "2.2.0"
    id("com.github.gmazzo.buildconfig") version "2.0.2"
    id("de.undercouch.download") version "4.1.2"
}

buildConfig {
    packageName("io.guthix.oldscape")
    buildConfigField(
        type ="java.nio.file.Path",
        name = "CACHE_PATH",
        value = "Path.of(\"${buildDir.path}\\cache\")".replace("\\", "\\\\")
    )
}

tasks { // download OpenRS2 archive
    val openRS2Id = 139 // cache id
    val downloadCache by registering(Download::class) {
        src("https://archive.openrs2.org/caches/$openRS2Id/disk.zip")
        dest(File(buildDir, "Osrs189.zip"))
        overwrite(false)
    }
    val unzipCache by registering(Copy::class) {
        dependsOn(downloadCache)
        from(zipTree(downloadCache.get().dest))
        into(buildDir)
    }
    val downloadXteas by registering(Download::class) {
        dependsOn(unzipCache)
        src("https://archive.openrs2.org/caches/$openRS2Id/keys.json")
        dest(File(buildDir, "cache/xteas.json"))
        overwrite(false)
    }
    getByName("processResources").dependsOn(unzipCache)
    getByName("processResources").dependsOn(downloadXteas)
    getByName("compileKotlin").dependsOn(unzipCache)
    getByName("compileKotlin").dependsOn(downloadXteas)
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    group = "io.guthix"
    version = "0.1.0"

    repositories {
        mavenCentral()
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        jvmToolchain(JavaVersion.VERSION_21.majorVersion.toInt())
    }
}

subprojects {
    dependencies {
        implementation(rootProject)
        implementation(rootProject.deps.kotlin.logging)
    }
}