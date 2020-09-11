plugins {
    application
}

val oldscapeServerVersion: String by rootProject.extra
val logbackVersion: String by rootProject.extra
val jacksonVersion: String by rootProject.extra

application { mainClass.set("io.guthix.oldscape.wiki.yaml.YamlDownloader") }

dependencies {
    implementation(rootProject)
    implementation(project(":plugins:core:combat"))
    implementation(project(":plugins:core:equipment"))
    implementation(project(":plugins:core:obj"))
    implementation(group = "io.guthix.oldscape", name = "oldscape-wiki-downloader", version = "0.1.0")
    implementation(group = "ch.qos.logback", name = "logback-classic", version = logbackVersion)
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = jacksonVersion)
    implementation(
        group = "com.fasterxml.jackson.dataformat", name = "jackson-dataformat-yaml", version = jacksonVersion
    )
    implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-kotlin", version = jacksonVersion)
}