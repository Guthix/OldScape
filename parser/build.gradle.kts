@file:Suppress("ConvertLambdaToReference")

val kotlinVersion: String by rootProject.extra

dependencies {
    implementation(group = "org.jetbrains.kotlin", name = "kotlin-reflect", version = kotlinVersion)
}