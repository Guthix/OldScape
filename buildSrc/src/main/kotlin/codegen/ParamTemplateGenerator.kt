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
package io.guthix.oldscape.codegen

import io.guthix.oldscape.*
import org.gradle.api.Project
import java.io.PrintWriter
import java.nio.file.Path
import kotlin.reflect.KClass

fun Project.writeParamTemplates(
    fileName: String,
    templateName: String,
    params: List<Map<Int, Any>>
) {
    val enumIds = readNamedIds(fileName)
    val sourceRoot = createSourceTree(this)
    val paramTypes = mutableMapOf<Int, KClass<*>>()
    params.forEach { paramMap -> paramMap.forEach { (id, value) ->
        paramTypes[id] = value::class
    } }
    sourceRoot.printCodeFile(templateName, enumIds, paramTypes)
}

private fun Path.printCodeFile(templateName: String, namedIds: List<NamedId>, paramTypes: Map<Int, KClass<*>>) {
    val sourceFile = resolve("${templateName}Params.kt").toFile()
    sourceFile.createNewFile()
    PrintWriter(sourceFile).use { pw ->
        pw.printFileHeader(ServerContextGenerator.packageName)
        pw.println()
        for ((id, configName) in namedIds) {
            val identifier = configNameToIdentifier(id, configName)
            val type = paramTypes[id] ?: throw IllegalStateException("Could not find param for $id.")
            pw.println("val $templateName.$identifier: ${type.simpleName}? get() = params[$id] as ${type.simpleName}?")
        }
        pw.flush()
    }
}
