@file:Suppress("ConvertLambdaToReference")

plugins {
    `embedded-kotlin`
}

val oldscapeCacheVersion: String by extra("0.1.0")
val jacksonVersion: String by extra("2.10.2")

repositories {
    mavenCentral()
}

dependencies {
    implementation(group = "io.guthix.oldscape", name = "oldscape-cache", version = oldscapeCacheVersion)
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = jacksonVersion)
    implementation(
        group = "com.fasterxml.jackson.dataformat", name = "jackson-dataformat-yaml", version = jacksonVersion
    )
    implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-kotlin", version = jacksonVersion)
}