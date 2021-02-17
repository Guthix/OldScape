plugins {
    application
}

val serverProject: Project = project(":server")

val logbackVersion: String by rootProject.extra
val ktSerVersion: String by serverProject.extra
val kamlVersion: String by serverProject.extra
val wikiDownloaderVersion: String by extra("0.1.0")

application { mainClass.set("io.guthix.oldscape.wiki.YamlDownloader") }

dependencies {
    implementation(project(":server:world"))
    implementation(project(":server:world:plugins:core:combat"))
    implementation(project(":server:world:plugins:core:equipment"))
    implementation(project(":server:world:plugins:core:monster"))
    implementation(project(":server:world:plugins:core:obj"))
    implementation(project(":server:world:plugins:core:stat"))
    implementation(project(":wiki:downloader"))
    implementation(group = "ch.qos.logback", name = "logback-classic", version = logbackVersion)
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version = ktSerVersion)
    implementation(group = "com.charleskorn.kaml", name = "kaml", version = kamlVersion)
}