plugins {
    application
}

val logbackVersion: String by project(":server").extra
val ktSerVersion: String by project(":server").extra
val kamlVersion: String by project(":server").extra
val wikiDownloaderVersion: String by extra("0.1.0")

application { mainClass.set("io.guthix.oldscape.wiki.yaml.YamlDownloader") }

dependencies {
    implementation(project(":server"))
    implementation(project(":server:plugins:core:combat"))
    implementation(project(":server:plugins:core:equipment"))
    implementation(project(":server:plugins:core:monster"))
    implementation(project(":server:plugins:core:obj"))
    implementation(project(":server:plugins:core:stat"))
    implementation(project(":wiki:downloader"))
    implementation(group = "ch.qos.logback", name = "logback-classic", version = logbackVersion)
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version = ktSerVersion)
    implementation(group = "com.charleskorn.kaml", name = "kaml", version = kamlVersion)
}