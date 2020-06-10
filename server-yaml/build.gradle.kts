plugins {
    application
}

val oldscapeServerVersion: String by rootProject.extra
val logbackVersion: String by rootProject.extra
val jacksonVersion: String by rootProject.extra


repositories {
    maven("https://jitpack.io")
}

application { mainClass.set("io.guthix.oldscape.wiki.yaml.YamlDownloader") }

dependencies {
    implementation(project(":downloader"))
    implementation(group = "com.github.guthix", name = "oldscape-server", version = oldscapeServerVersion)
    implementation(group = "ch.qos.logback", name = "logback-classic", version = logbackVersion)
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = jacksonVersion)
    implementation(
        group = "com.fasterxml.jackson.dataformat", name = "jackson-dataformat-yaml", version = jacksonVersion
    )
    implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-kotlin", version = jacksonVersion)
}