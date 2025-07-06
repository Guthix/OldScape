import io.guthix.oldscape.ServerContextGenerator

plugins {
    application
    kotlin("plugin.serialization") version "2.2.0"
}

apply<ServerContextGenerator>()

application {
    mainClass.set("io.guthix.oldscape.server.OldScape")
}

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
    api(deps.kotlin.serialization.json)
    implementation(deps.kotlin.serialization.yaml)
    api(deps.kotlin.reflect)
    implementation(deps.kotlin.scripting)
    implementation(deps.kotlin.coroutines)
    implementation(deps.netty.all)
    implementation(deps.classgraph)
    implementation(deps.logback)
    implementation(deps.exposed.core)
    implementation(deps.exposed.jdbc)
    implementation(deps.postgresql)
}