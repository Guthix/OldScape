@file:Suppress("ConvertLambdaToReference")

val kCoroutinesVersion: String by rootProject.extra
val kotlinVersion: String by rootProject.extra
val ktorVersion: String by extra("1.4.0")

kotlin { explicitApi() }

dependencies {
    api(project(":wiki:parser"))
    api(project(":cache"))
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = kCoroutinesVersion)
    implementation(group = "io.ktor", name = "ktor-server-core", version = ktorVersion)
    implementation(group = "io.ktor", name = "ktor-client-apache", version = ktorVersion)
}