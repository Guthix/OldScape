@file:Suppress("ConvertLambdaToReference")

allprojects {
    dependencies {
        implementation(rootProject)
    }

    gradle.buildFinished { if(!buildFile.exists()) buildDir.deleteRecursively() }
}