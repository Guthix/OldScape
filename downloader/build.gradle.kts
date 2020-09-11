@file:Suppress("ConvertLambdaToReference")

val oldscapeCacheVersion: String by rootProject.extra
val kotlinCoroutinesVersion: String by rootProject.extra
val ktorVersion: String by rootProject.extra

kotlin { explicitApi() }

dependencies {
    api(project(":parser"))
    api(group = "io.guthix.oldscape", name = "oldscape-cache", version = oldscapeCacheVersion)
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = kotlinCoroutinesVersion)
    implementation(group = "io.ktor", name = "ktor-server-core", version = ktorVersion)
    implementation(group = "io.ktor", name = "ktor-client-apache", version = ktorVersion)
}