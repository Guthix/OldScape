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
package io.guthix.oldscape.server.template.imp

import io.guthix.oldscape.cache.config.NamedConfig
import io.guthix.oldscape.server.template.configNameToIdentifier
import io.guthix.oldscape.server.template.createSourceTree
import io.guthix.oldscape.server.template.printFileHeader
import org.gradle.api.Project
import java.io.PrintWriter
import java.nio.file.Path

private const val maxTemplatePerFile = 10000

fun Project.writeNamedConfigTemplates(name: String, configs: Map<Int, NamedConfig>, ignoreNulls: Boolean) {
    val sourceRoot = createSourceTree(this)
    sourceRoot.toFile().mkdirs()
    sourceRoot.printCodeFile("${name}Ids", configs, ignoreNulls)
}

private fun Path.printCodeFile(fileName: String, configs: Map<Int, NamedConfig>, ignoreNulls: Boolean) {
    val sourceFile = resolve("$fileName.kt").toFile()
    sourceFile.createNewFile()
    PrintWriter(sourceFile).use { pw ->
        pw.printFileHeader()
        pw.println()
        pw.println("object $fileName {")
        for (namedConfig in configs.values) {
            val identifier = configNameToIdentifier(namedConfig.id, namedConfig.name)
            if (namedConfig.name.equals("null", ignoreCase = true) && ignoreNulls) continue
            pw.println("    const val $identifier: Int = ${namedConfig.id}")
        }
        pw.println("}")
    }
}