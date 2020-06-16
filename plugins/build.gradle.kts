@file:Suppress("ConvertLambdaToReference")

subprojects {
    dependencies {
        implementation(rootProject)
    }

    gradle.buildFinished { if (!buildFile.exists()) buildDir.deleteRecursively() }
}

gradle.buildFinished { buildDir.deleteRecursively() }