@file:Suppress("ConvertLambdaToReference")

import io.guthix.oldscape.registerPublication

allprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "org.jetbrains.dokka")

    kotlin { explicitApi() }

    java {
        withJavadocJar()
        withSourcesJar()
    }

    registerPublication(name = "oldscape-wiki", description = "A library for dumping the Oldschool Runescape Wiki")
}