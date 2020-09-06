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
package io.guthix.oldscape.server.cache

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.guthix.cache.js5.Js5Cache
import io.guthix.cache.js5.container.disk.Js5DiskStore
import io.guthix.oldscape.cache.ConfigArchive
import io.guthix.oldscape.cache.config.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.io.IOException
import java.io.PrintWriter
import java.nio.file.Path

class CodeGenerator : Plugin<Project> {
    override fun apply(target: Project) {
        target.task("sourceGen") { task ->
            task.doFirst {
                val resourceDir = "${target.projectDir}/src/main/resources"
                Js5Cache(Js5DiskStore.open(File("$resourceDir/cache").toPath())).use { cache ->
                    val configArchive = cache.readArchive(ConfigArchive.id)

                    val locs = LocationConfig.load(configArchive.readGroup(LocationConfig.id))
                    target.writeNamedConfigTemplates("Loc", locs)

                    val objs = ObjectConfig.load(configArchive.readGroup(ObjectConfig.id))
                    target.writeNamedConfigTemplates("Obj", objs)

                    val npcs = NpcConfig.load(configArchive.readGroup(NpcConfig.id))
                    target.writeNamedConfigTemplates("Npc", npcs)

                    val enums = EnumConfig.load(configArchive.readGroup(EnumConfig.id))
                    target.writeEnumTemplates(enums, objs, locs)
                }
            }
        }
    }



    private fun createSourceTree(target: Project): Path {
        val srcDir = File("${target.projectDir}/src/main/kotlin").toPath()
        return srcDir.resolve(packageDir)
    }

    data class EnumEngineTemplate(val id: Int, val name: String)

    private fun Project.writeEnumTemplates(
        enumConfigs: Map<Int, EnumConfig<Any, Any>>,
        objConfigs: Map<Int, ObjectConfig>,
        locConfigs: Map<Int, LocationConfig>
    ) {
        val resourceDir = "${projectDir}/src/main/resources"
        val enumEngineTemplates = ObjectMapper(YAMLFactory()).registerKotlinModule()
            .readValue(File(resourceDir).toPath().resolve("template/Enums.yaml").toFile(),
                object : TypeReference<List<EnumEngineTemplate>>() {}
            )
        val sourceRoot = createSourceTree(this)
        sourceRoot.toFile().mkdirs()
        sourceRoot.printEnumTemplates("EnumTemplate", enumConfigs, enumEngineTemplates, objConfigs, locConfigs)
    }

    private fun Path.printEnumTemplates(
        name: String,
        enumConfigs: Map<Int, EnumConfig<Any, Any>>,
        enumEngineTemplates: List<EnumEngineTemplate>,
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

        val sourceFile = resolve("${name}s.kt").toFile()
        PrintWriter(sourceFile).use { pw ->
            pw.println(warningHeader)
            pw.println("@file:Suppress(\"UNCHECKED_CAST\")")
            pw.println("package $packageName")
            pw.println()
            pw.println("import io.guthix.oldscape.cache.config.EnumConfig")
            pw.println("import io.guthix.oldscape.server.template.*")
            pw.println()
            pw.println("object ${name}s : TemplateLoader<$name<Any, Any>>() {")
            val enumsToWrite = enumEngineTemplates.toMutableList()
            while (enumsToWrite.isNotEmpty()) {
                fun writeEnum(extraConfig: EnumEngineTemplate) {
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
                        "Could not find enum $name ${extraConfig.id}."
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

    private fun Project.writeNamedConfigTemplates(name: String, configs: Map<Int, NamedConfig>) {
        val sourceRoot = createSourceTree(this)
        sourceRoot.toFile().mkdirs()
        sourceRoot.printNamedConfigTemplates("${name}Template", configs)
    }

    private fun Path.printNamedConfigTemplates(name: String, configs: Map<Int, NamedConfig>) {
        val sourceFile = resolve("${name}s.kt").toFile()
        sourceFile.createNewFile()
        PrintWriter(sourceFile).use { pw ->
            pw.println(warningHeader)
            pw.println("@file:Suppress(\"PropertyName\")")
            pw.println("package $packageName")
            pw.println()
            pw.println("import io.guthix.oldscape.server.template.*")
            pw.println()
            pw.println("object ${name}s : TemplateLoader<$name>() {")
            for ((id, config) in configs) {
                val identifier = configNameToIdentifier(id, config.name)
                if (identifier.contains("null", ignoreCase = true)) continue
                pw.println("    val $identifier: $name get() = get(${config.id})")
            }
            pw.println("}")
            pw.flush()
        }
    }

    private fun configNameToIdentifier(id: Int, name: String): String {
        val normalizedName = name.toUpperCase().replace(' ', '_').replace(Regex("[^a-zA-Z\\d:]"), "").removeTags()
        val propName = if (normalizedName.isNotEmpty()) normalizedName + "_$id" else "$id"
        return if (propName.first().isDigit()) "`$propName`" else propName
    }

    fun String.removeTags(): String {
        val builder = StringBuilder(length)
        var inTag = false
        forEach {
            if (it == '<') {
                inTag = true
            } else if (it == '>') {
                inTag = false
            } else if (!inTag) {
                builder.append(it)
            }
        }
        return "$builder"
    }

    companion object {
        private const val packageName: String = "io.guthix.oldscape.server.content"

        private val warningHeader: String =
            "/* Dont EDIT! This file is automatically generated by ${CodeGenerator::class.qualifiedName}. */"

        private val packageDir: String = packageName.replace(".", "/")
    }
}