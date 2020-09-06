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

import io.guthix.oldscape.cache.config.EnumConfig
import io.guthix.oldscape.cache.config.LocationConfig
import io.guthix.oldscape.cache.config.ObjectConfig
import io.guthix.oldscape.server.template.NamedId
import io.guthix.oldscape.server.template.printFileHeader
import io.guthix.oldscape.server.template.configNameToIdentifier
import io.guthix.oldscape.server.template.createSourceTree
import io.guthix.oldscape.server.template.readNamedIds
import org.gradle.api.Project
import java.io.IOException
import java.io.PrintWriter
import java.nio.file.Path


fun Project.writeEnumTemplates(
    enumConfigs: Map<Int, EnumConfig<Any, Any>>,
    objConfigs: Map<Int, ObjectConfig>,
    locConfigs: Map<Int, LocationConfig>
) {
    val enumIds = readNamedIds("Enums")
    val sourceRoot = createSourceTree(this)
    sourceRoot.printCodeFile("EnumTemplate", enumConfigs, enumIds, objConfigs, locConfigs)
}

private fun Path.printCodeFile(
    templateName: String,
    enumConfigs: Map<Int, EnumConfig<Any, Any>>,
    enumEngineTemplates: List<NamedId>,
    objConfigs: Map<Int, ObjectConfig>,
    locConfigs: Map<Int, LocationConfig>
) {
    fun getGeneratedName(enumId: Int, type: EnumConfig.Type?, value: Any): String = when (type) {
        EnumConfig.Type.OBJ, EnumConfig.Type.NAMED_OBJ -> {
            val id = value as Int
            val configName = objConfigs[id]?.name ?: throw IllegalStateException(
                "Could not find obj for id $id in enum $enumId."
            )
            val configId = configNameToIdentifier(id, configName)
            if (configId.contains("null", ignoreCase = true)) "$id" else "ObjId.$configId"
        }
        EnumConfig.Type.LOC -> {
            val id = value as Int
            val configName = locConfigs[id]?.name ?: throw IllegalStateException(
                "Could not find loc for id $id in enum $enumId."
            )
            val configId = configNameToIdentifier(id, configName)
            if (configId.contains("null", ignoreCase = true)) "$id" else "LocId.$configId"
        }
        EnumConfig.Type.ENUM -> {
            val id = value as Int
            enumEngineTemplates.find { it.id == id }?.name ?: throw IllegalStateException(
                "Could not find enum for id $id in enum $enumId."
            )
        }
        else -> "$value"
    }

    fun getEnumType(enumId: Int, type: EnumConfig.Type?, firstValue: Any): String = when (type) {
        EnumConfig.Type.COMPONENT -> "EnumConfig.Component"
        EnumConfig.Type.STAT -> "EnumConfig.Stat"
        EnumConfig.Type.COORDINATE -> "EnumConfig.Coord"
        EnumConfig.Type.ENUM -> {
            val firstId = firstValue as Int
            val firstEnum = enumConfigs[firstId] ?: throw IllegalStateException(
                "Could not find enum for $firstId in enum $enumId."
            )
            "Map<${getEnumType(firstId, firstEnum.keyType, firstEnum.keyValuePairs.keys.first())}, " +
                "${getEnumType(firstId, firstEnum.valType, firstEnum.keyValuePairs.values.first())}>"
        }
        else -> "Int"
    }

    val sourceFile = resolve("${templateName}s.kt").toFile()
    PrintWriter(sourceFile).use { pw ->
        pw.printFileHeader()
        pw.println("import io.guthix.oldscape.cache.config.EnumConfig")
        pw.println()
        pw.println("object ${templateName}s : TemplateLoader<$templateName<Any, Any>>() {")
        val enumsToWrite = enumEngineTemplates.toMutableList()
        while (enumsToWrite.isNotEmpty()) {
            fun writeEnum(extraConfig: NamedId) {
                fun writeDependentEnums(type: EnumConfig.Type?, values: Collection<Any>) {
                    if (type == EnumConfig.Type.ENUM) {
                        val keyEnumsToWrite = enumsToWrite.filter { (id) ->
                            values.any { it == id }
                        }
                        keyEnumsToWrite.forEach { config ->
                            writeEnum(config)
                            enumsToWrite.remove(config)
                        }
                    }
                }

                val enum = enumConfigs[extraConfig.id] ?: throw IOException(
                    "Could not find enum $templateName ${extraConfig.id}."
                )
                writeDependentEnums(enum.keyType, enum.keyValuePairs.keys)
                writeDependentEnums(enum.valType, enum.keyValuePairs.values)
                val keyType = getEnumType(extraConfig.id, enum.keyType, enum.keyValuePairs.keys.first())
                val valueType = getEnumType(extraConfig.id, enum.valType, enum.keyValuePairs.values.first())

                if (enum.keyType == EnumConfig.Type.ENUM || enum.valType == EnumConfig.Type.ENUM) {
                    pw.println("    val ${extraConfig.name}: Map<$keyType, $valueType> get() = mapOf(")
                    enum.keyValuePairs.forEach { (key, value) ->
                        val curKeyName = getGeneratedName(extraConfig.id, enum.keyType, key)
                        val curValueName = getGeneratedName(extraConfig.id, enum.valType, value)
                        pw.println("        $curKeyName to $curValueName,")
                    }
                    pw.println("    )")
                } else {
                    pw.println("    val ${extraConfig.name}: Map<$keyType, $valueType> get() = " +
                        "get(${extraConfig.id}) as Map<$keyType, $valueType>"
                    )
                }
            }
            writeEnum(enumsToWrite.removeAt(0))
        }
        pw.println("}")
        pw.flush()
    }
}