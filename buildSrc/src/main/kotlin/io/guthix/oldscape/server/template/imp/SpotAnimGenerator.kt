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

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.guthix.oldscape.server.template.configNameToIdentifier
import io.guthix.oldscape.server.template.createSourceTree
import io.guthix.oldscape.server.template.printFileHeader
import org.gradle.api.Project
import java.io.File
import java.io.PrintWriter
import java.nio.file.Path


data class SpotAnimConfig(val id: Int, val name: String, val height: Int?)

fun Project.readSpotAnimConfig(name: String): List<SpotAnimConfig> {
    val resourceDir = "${projectDir}/src/main/resources"
    return ObjectMapper(YAMLFactory()).registerKotlinModule()
        .readValue(
            File(resourceDir).toPath().resolve("template/$name.yaml").toFile(),
            object : TypeReference<List<SpotAnimConfig>>() {}
        )
}

fun Project.writeSpotAnimTemplates(
    fileName: String,
    templateName: String,
    physTemplateName: String,
) {
    val enumIds = readSpotAnimConfig(fileName)
    val sourceRoot = createSourceTree(this)
    sourceRoot.printCodeFile(templateName, physTemplateName, enumIds)
}

private fun Path.printCodeFile(templateName: String, physTemplateName: String, namedIds: List<SpotAnimConfig>) {
    val sourceFile = resolve("${templateName}s.kt").toFile()
    sourceFile.createNewFile()
    PrintWriter(sourceFile).use { pw ->
        pw.printFileHeader()
        pw.println()
        pw.println("object ${templateName}s : TemplateLoader<$templateName>() {")
        for ((id, configName, height) in namedIds) {
            if (height == null) {
                val identifier = configNameToIdentifier(id, configName)
                pw.println("    val $identifier: $templateName get() = get(${id})")
            } else {
                val identifier = nameToPhysicalIdentifier(id, configName, height)
                pw.println("    val $identifier: $physTemplateName get() = $physTemplateName(get(${id}), $height)")
            }
        }
        pw.println("}")
        pw.flush()
    }
}

private fun nameToPhysicalIdentifier(id: Int, configName: String, height: Int): String {
    val normalizedName = configName.toUpperCase().replace(' ', '_')
    return "${normalizedName}_H${height}_$id"
}