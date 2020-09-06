/*
 * Copyright 2018-2020 Guthix
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
package io.guthix.oldscape.server.template.imp

import io.guthix.oldscape.cache.config.NamedConfig
import io.guthix.oldscape.server.template.printFileHeader
import io.guthix.oldscape.server.template.configNameToIdentifier
import io.guthix.oldscape.server.template.createSourceTree
import org.gradle.api.Project
import java.io.PrintWriter
import java.nio.file.Path

fun Project.writeNamedConfigTemplates(name: String, configs: Map<Int, NamedConfig>) {
    val sourceRoot = createSourceTree(this)
    sourceRoot.toFile().mkdirs()
    sourceRoot.printCodeFile("${name}Template", configs)
}

private fun Path.printCodeFile(templateName: String, configs: Map<Int, NamedConfig>) {
    val sourceFile = resolve("${templateName}s.kt").toFile()
    sourceFile.createNewFile()
    PrintWriter(sourceFile).use { pw ->
        pw.printFileHeader()
        pw.println()
        pw.println("object ${templateName}s : TemplateLoader<$templateName>() {")
        for ((id, config) in configs) {
            val identifier = configNameToIdentifier(id, config.name)
            if (identifier.contains("null", ignoreCase = true)) continue
            pw.println("    val $identifier: $templateName get() = get(${config.id})")
        }
        pw.println("}")
        pw.flush()
    }
}