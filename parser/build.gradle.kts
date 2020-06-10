@file:Suppress("ConvertLambdaToReference")

plugins {
    `maven-publish`
}

val kotlinVersion: String by rootProject.extra

dependencies {
    implementation(group = "org.jetbrains.kotlin", name = "kotlin-reflect", version = kotlinVersion)
}

kotlin { explicitApi() }

val dokkaJar: Jar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    classifier = "javadoc"
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