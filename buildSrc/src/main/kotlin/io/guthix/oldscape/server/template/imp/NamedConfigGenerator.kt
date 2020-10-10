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
import io.guthix.oldscape.server.template.configNameToIdentifier
import io.guthix.oldscape.server.template.createSourceTree
import io.guthix.oldscape.server.template.printFileHeader
import org.gradle.api.Project
import java.io.PrintWriter
import java.nio.file.Path

private const val maxTemplatePerFile = 10000

fun Project.writeNamedConfigTemplates(name: String, configs: Map<Int, NamedConfig>) {
    val sourceRoot = createSourceTree(this)
    sourceRoot.toFile().mkdirs()
    sourceRoot.printCodeFile("${name}Template", configs)
}

private fun Path.printCodeFile(templateName: String, configs: Map<Int, NamedConfig>) {
    val baseFile = resolve("${templateName}s.kt").toFile()
    baseFile.createNewFile()
    PrintWriter(baseFile).use { pw ->
        pw.printFileHeader()
        pw.println()
        pw.println("object ${templateName}s : TemplateLoader<$templateName>() ")
    }
    configs.values.chunked(maxTemplatePerFile).forEachIndexed { index, chunkedConfigs ->
        val sourceFile = resolve("${templateName}${index}s.kt").toFile()
        sourceFile.createNewFile()
        PrintWriter(sourceFile).use { pw ->
            pw.printFileHeader()
            pw.println()
            for (config in chunkedConfigs) {
                val identifier = configNameToIdentifier(config.id, config.name)
                pw.println("val ${templateName}s.$identifier: $templateName get() = get(${config.id})")
            }
            pw.flush()
        }
    }

}