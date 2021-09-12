import io.guthix.oldscape.ServerContextGenerator

plugins {
    application
    kotlin("plugin.serialization")
}

apply<ServerContextGenerator>()

application {
    mainClass.set("io.guthix.oldscape.server.OldScape")
}

val serverProject: Project = project(":server")

val kCoroutinesVersion: String by rootProject.extra
val logbackVersion: String by rootProject.extra
val nettyVersion: String by serverProject.extra
val ktSerVersion: String by serverProject.extra
val kamlVersion: String by serverProject.extra
val exposedVersion: String by serverProject.extra
val postgresVersion: String by serverProject.extra
val kotlinVersion: String by serverProject.rootProject.extra
val classGraphVersion: String by extra("4.8.53")

allprojects {
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    project(":server:world:plugins").dependencyProject.subprojects.forEach { pluginProject ->
        if (pluginProject.buildFile.exists()) {
            runtimeOnly(pluginProject)
        }
    }
    api(project(":cache"))
    api(project(":dim"))
    api(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version = ktSerVersion)
    api(group = "org.jetbrains.kotlin", name = "kotlin-reflect", version = kotlinVersion)
    implementation(group = "org.jetbrains.kotlin", name = "kotlin-scripting-common", version = kotlinVersion)
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = kCoroutinesVersion)
    implementation(group = "io.netty", name = "netty-all", version = nettyVersion)
    implementation(group = "io.github.classgraph", name = "classgraph", version = classGraphVersion)
    implementation(group = "ch.qos.logback", name = "logback-classic", version = logbackVersion)
    implementation(group = "com.charleskorn.kaml", name = "kaml", version = kamlVersion)
    implementation(group = "org.jetbrains.exposed", name = "exposed-core", version = exposedVersion)
    implementation(group = "org.jetbrains.exposed", name = "exposed-jdbc", version = exposedVersion)
    implementation(group = "org.postgresql", name = "postgresql", version = postgresVersion)
}