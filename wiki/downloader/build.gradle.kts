@file:Suppress("ConvertLambdaToReference")

kotlin { explicitApi() }

dependencies {
    api(project(":wiki:parser"))
    api(project(":cache"))
    implementation(deps.kotlin.coroutines)
    implementation(deps.ktor.server.core)
    implementation(deps.ktor.client.apache)
}