@file:Suppress("ConvertLambdaToReference")

subprojects {
    dependencies {
        implementation(project(":server"))
    }

    gradle.buildFinished { if (!buildFile.exists()) buildDir.deleteRecursively() }
}

gradle.buildFinished { buildDir.deleteRecursively() }