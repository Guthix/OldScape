@file:Suppress("ConvertLambdaToReference")

import io.guthix.oldscape.registerPublication

plugins {
    `maven-publish`
    signing
}

kotlin { explicitApi() }

dependencies {
    api(project(":cache:names"))
    api(deps.jagex.js5)
    implementation(deps.logback)
}

registerPublication(name = "oldscape-cache", description = "A library for modifying OldScape caches")