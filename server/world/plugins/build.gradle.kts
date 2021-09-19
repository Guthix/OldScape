subprojects {
    dependencies {
        implementation(project(":server:world"))
    }

    gradle.buildFinished { if (!buildFile.exists()) buildDir.deleteRecursively() }
}

gradle.buildFinished { buildDir.deleteRecursively() }