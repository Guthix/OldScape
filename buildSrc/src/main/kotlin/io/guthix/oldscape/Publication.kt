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
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.provider.Provider
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.gradle.plugins.signing.SigningExtension
import java.net.URI

@Suppress("ConvertLambdaToReference")
fun Project.registerPublication(publicationName: String, pomName: String) {
    configure<PublishingExtension> {
        repositories {
            mavenCentralRepository()
        }
        publications {
            val publicationProvider = register<MavenPublication>(publicationName) {
                configurePom(pomName, description, components.getByName("java"))
            }
            signPublication(publicationProvider)
        }
    }
}

private fun RepositoryHandler.mavenCentralRepository() = maven {
    name = "MavenCentral"
    url = URI("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
    credentials {
        username = System.getenv("OSSRH_USERNAME")
        password = System.getenv("OSSRH_PASSWORD")
    }
}

private fun MavenPublication.configurePom(projectName: String, desc: String?, component: SoftwareComponent) {
    pom {
        name.set(projectName)
        desc?.let { description.set(desc) }
        url.set("https://github.com/guthix/OldScape")
        from(component)
        licenses {
            license {
                name.set("The Apache Software License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("Guthix")
                name.set("Guthix Contributors")
                organization.set("Guthix")
                organizationUrl.set("http://www.guthix.io")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/guthix/oldscape.git")
            url.set("https://github.com/guthix/oldscape/tree/master")
        }
    }
}

@Suppress("UnstableApiUsage")
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