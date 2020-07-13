@file:Suppress("ConvertLambdaToReference")

plugins {
    `maven-publish`
}

repositories {
    maven("https://jitpack.io")
}

val oldscapeCacheVersion: String by rootProject.extra
val kotlinCoroutinesVersion: String by rootProject.extra
val ktorVersion: String by rootProject.extra

kotlin { explicitApi() }

dependencies {
    api(project(":parser"))
    api(group = "com.github.guthix", name = "oldscape-cache", version = oldscapeCacheVersion)
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = kotlinCoroutinesVersion)
    implementation(group = "io.ktor", name = "ktor-server-core", version = ktorVersion)
    implementation(group = "io.ktor", name = "ktor-client-apache", version = ktorVersion)
}

val dokkaJar: Jar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    archiveClassifier.set("javadoc")
    from(tasks.dokka)
}


publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
            artifact(dokkaJar)
            pom {
                url.set("https://github.com/guthix/OldScape-Wiki")
                licenses {
                    license {
                        name.set("GNU Lesser General Public License v3.0")
                        url.set("https://www.gnu.org/licenses/lgpl-3.0.txt")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/guthix/OldScape-Wiki.git")
                    developerConnection.set("scm:git:ssh://github.com/guthix/OldScape-Wiki.git")
                }
            }
        }
    }
}