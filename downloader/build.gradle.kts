repositories {
    maven("https://jitpack.io")
}

val oldscapeCacheVersion: String by rootProject.extra
val kotlinCoroutinesVersion: String by rootProject.extra
val ktorVersion: String by rootProject.extra

dependencies {
    implementation(rootProject)
    api(group = "com.github.guthix", name = "oldscape-cache", version = oldscapeCacheVersion)
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = kotlinCoroutinesVersion)
    implementation(group = "io.ktor", name = "ktor-server-core", version = ktorVersion)
    implementation(group = "io.ktor", name = "ktor-client-apache", version = ktorVersion)
}