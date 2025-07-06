/*
 * Copyright 2018-2021 Guthix
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.guthix.oldscape

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.provider.Provider
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.gradle.plugins.signing.SigningExtension
import java.net.URI

private const val SNAPSHOT_BASE_VERSION = "0.2.1"

fun Project.registerPublication(name: String, description: String) {
    this.description = description
    configure<PublishingExtension> {
        repositories {
            mavenCentralRepository()
            sonarSnapshotRepository()
        }
        publications {
            val taskName = name.split("-").joinToString("", transform = { sub ->
                sub.replaceFirstChar { char -> char.titlecase() }
            })
            val publicationProvider = register<MavenPublication>(taskName) {
                configurePom(name, description, components.getByName("java"))
            }
            signPublication(publicationProvider)
        }
    }
}

private fun RepositoryHandler.mavenCentralRepository() = maven {
    name = "MavenCentral"
    url = URI("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
    credentials { ossrhCredentials() }
}

private fun RepositoryHandler.sonarSnapshotRepository() = maven {
    name = "SonarSnapshot"
    url = URI("https://oss.sonatype.org/content/repositories/snapshots/")
    credentials { ossrhCredentials() }
}

private fun PasswordCredentials.ossrhCredentials() {
    username = System.getenv("OSSRH_USERNAME")
    password = System.getenv("OSSRH_PASSWORD")
}

private fun MavenPublication.configurePom(projectName: String, desc: String?, component: SoftwareComponent) {
    val releaseVersion = System.getenv("RELEASE_VERSION")?.removePrefix("v")
    version = releaseVersion ?: "$SNAPSHOT_BASE_VERSION.${System.getenv("GITHUB_RUN_NUMBER") ?: "LOCAL"}-SNAPSHOT"
    pom {
        name.set(projectName)
        artifactId = projectName
        desc?.let { description.set(desc) }
        url.set("https://github.com/guthix/OldScape")
        from(component)
        licenses {
            license {
                name.set("The Apache Software License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("Guthix")
                name.set("Guthix Contributors")
                organization.set("Guthix")
                organizationUrl.set("https://www.guthix.io")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/guthix/oldscape.git")
            url.set("https://github.com/guthix/oldscape/tree/master")
        }
    }
}

private fun Project.signPublication(publicationProvider: Provider<MavenPublication>) {
    val signingKey = System.getenv("SIGNING_KEY")
    val signingKeyPassphrase = System.getenv("SIGNING_PASSWORD")
    if (!signingKey.isNullOrBlank()) {
        extensions.configure<SigningExtension>("signing") {
            useInMemoryPgpKeys(signingKey, signingKeyPassphrase)
            sign(publicationProvider.get())
        }
    }
}