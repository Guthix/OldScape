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

fun Project.writeIntTemplates(
    fileName: String,
    templateName: String
) {
    val enumIds = readNamedIds(fileName)
    val sourceRoot = createSourceTree(this)
    sourceRoot.printCodeFile(templateName, enumIds)
}

private fun Path.printCodeFile(templateName: String, namedIds: List<NamedId>) {
    val sourceFile = resolve("${templateName}s.kt").toFile()
    sourceFile.createNewFile()
    PrintWriter(sourceFile).use { pw ->
        pw.printFileHeader(IdGenerator.PACKAGE_NAME)
        pw.println()
        pw.println("object ${templateName}s {")
        for ((id, configName) in namedIds) {
            val identifier = configNameToIdentifier(id, configName)
            pw.println("    const val $identifier: Int = $id")
        }
        pw.println("}")
        pw.flush()
    }
}