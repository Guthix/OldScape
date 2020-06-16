version = rootProject.version
description = "Server Blueprints"

val oldscapeCacheVersion: String by rootProject.extra

dependencies {
    implementation(project(":dimensions"))
    api(group = "com.github.guthix", name = "oldscape-cache", version = oldscapeCacheVersion)
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
                url.set("https://github.com/guthix/OldScape-Server")
                licenses {
                    license {
                        name.set("GNU Lesser General Public License v3.0")
                        url.set("https://www.gnu.org/licenses/lgpl-3.0.txt")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/guthix/OldScape-Server.git")
                    developerConnection.set("scm:git:ssh://github.com/guthix/OldScape-Server.git")
                }
            }
        }
    }
}