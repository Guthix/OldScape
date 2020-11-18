plugins {
    application
}

val logbackVersion: String by rootProject.extra
val ktSerVersion: String by rootProject.extra
val kamlVersion: String by rootProject.extra
val wikiDownloaderVersion: String by extra("0.1.0")

application { mainClass.set("io.guthix.oldscape.wiki.yaml.YamlDownloader") }

dependencies {
    implementation(rootProject)
    implementation(project(":plugins:core:combat"))
    implementation(project(":plugins:core:equipment"))
    implementation(project(":plugins:core:obj"))
    implementation(group = "io.guthix.oldscape", name = "oldscape-wiki-downloader", version = wikiDownloaderVersion)
    implementation(group = "ch.qos.logback", name = "logback-classic", version = logbackVersion)
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version = ktSerVersion)
    implementation(group = "com.charleskorn.kaml", name = "kaml", version = kamlVersion)
}