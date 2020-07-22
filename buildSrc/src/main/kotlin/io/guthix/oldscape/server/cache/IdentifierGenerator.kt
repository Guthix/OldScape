/*
 * This file is part of Guthix OldScape-Server.
 *
 * Guthix OldScape-Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Guthix OldScape-Server. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.cache

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.guthix.cache.js5.Js5Archive
import io.guthix.cache.js5.Js5Cache
import io.guthix.cache.js5.container.disk.Js5DiskStore
import io.guthix.oldscape.cache.ConfigArchive
import io.guthix.oldscape.cache.config.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSetContainer
import java.io.File
import java.io.IOException
import java.io.PrintWriter
import java.nio.file.Path

class IdentifierGenerator : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.withType(JavaPlugin::class.java) {
            val sourceSets = target.properties["sourceSets"] as SourceSetContainer
            sourceSets.getByName("main").java.srcDir("src/main/generated")
        }
        val task = target.task("sourceGen") { task ->
            task.doFirst {
                val sourceRoot = createSourceTree(target)
                val resourceDir = "${target.projectDir}/src/main/resources"

                val ds = Js5DiskStore.open(File("$resourceDir/cache").toPath())
                val cache = Js5Cache(ds)
                val configArchive = cache.readArchive(ConfigArchive.id)

                val objs = ObjectConfig.load(configArchive.readGroup(ObjectConfig.id))
                val npcs = NpcConfig.load(configArchive.readGroup(NpcConfig.id))
                val locs = LocationConfig.load(configArchive.readGroup(LocationConfig.id))
                sourceRoot.toFile().mkdirs()
                sourceRoot.generateSourceIds("LocId", locs)
                sourceRoot.generateSourceIds("NpcId", npcs)
                sourceRoot.generateSourceIds("ObjId", objs)
                sourceRoot.generateEnums("Enums", File(resourceDir).toPath(), configArchive, objs, locs)
            }
        }
        val compileKotlinTask = target.tasks.getByName("compileKotlin")
        compileKotlinTask.dependsOn(task)
    }

    private fun createSourceTree(target: Project): Path {
        val srcDir = File("${target.projectDir}/src/main/generated").toPath()
        return srcDir.resolve(packageDir)
    }

    data class ExtraEnumConfig(val id: Int, val name: String)

    private fun Path.generateEnums(
        name: String,
        resourceDir: Path,
        configArchive: Js5Archive,
        objConfigs: Map<Int, ObjectConfig>,
        locConfigs: Map<Int, LocationConfig>,
    ) {
        val enums = EnumConfig.load(configArchive.readGroup(EnumConfig.id))
        val mapper = ObjectMapper(YAMLFactory()).registerKotlinModule()
        val extraEnumConfigs = mapper.readValue(
            resourceDir.resolve("config/enums/Enums.yaml").toFile(), object : TypeReference<List<ExtraEnumConfig>>() {}
        )
        val codeFile = resolve("$name.kt").toFile()

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
                extraEnumConfigs.find { it.id == id }?.name ?: throw IllegalStateException(
                    "Could not find enum for id $id in enum $enumId."
                )
            }
            else -> "$value"
        }

        fun getEnumType(enumId: Int, type: EnumConfig.Type?, firstValue: Any): String = when (type) {
            EnumConfig.Type.COMPONENT -> EnumConfig.Component::class.simpleName ?: ""
            EnumConfig.Type.STAT -> EnumConfig.Stat::class.simpleName ?: ""
            EnumConfig.Type.COORDINATE -> EnumConfig.Coord::class.simpleName ?: ""
            EnumConfig.Type.ENUM -> {
                val firstId = firstValue as Int
                val firstEnum = enums[firstId] ?: throw IllegalStateException(
                    "Could not find enum for $firstId in enum $enumId."
                )
                "Map<${getEnumType(firstId, firstEnum.keyType, firstEnum.keyValuePairs.keys.first())}, " +
                    "${getEnumType(firstId, firstEnum.valType, firstEnum.keyValuePairs.values.first())}>"
            }
            else -> Int::class.simpleName ?: ""
        }

        PrintWriter(codeFile).apply {
            println("/* This file is automatically generated by ${IdentifierGenerator::class.qualifiedName}. */")
            println("package $packageName")
            println()
            println("import io.guthix.oldscape.server.api.EnumBlueprints")
            println("import ${EnumConfig::class.qualifiedName}.*")
            println()
            println("@Suppress(\"UNCHECKED_CAST\")")
            println("object $name {")
            val enumsToWrite = extraEnumConfigs.toMutableList()
            while (enumsToWrite.isNotEmpty()) {
                fun writeEnum(extraConfig: ExtraEnumConfig) {
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

                    val enum = enums[extraConfig.id] ?: throw IOException(
                        "Could not find enum $name ${extraConfig.id}."
                    )
                    writeDependentEnums(enum.keyType, enum.keyValuePairs.keys)
                    writeDependentEnums(enum.valType, enum.keyValuePairs.values)
                    val keyType = getEnumType(extraConfig.id, enum.keyType, enum.keyValuePairs.keys.first())
                    val valueType = getEnumType(extraConfig.id, enum.valType, enum.keyValuePairs.values.first())

                    if (enum.keyType == EnumConfig.Type.ENUM || enum.valType == EnumConfig.Type.ENUM) {
                        println("    val ${extraConfig.name}: Map<$keyType, $valueType> = mapOf(")
                        enum.keyValuePairs.forEach { (key, value) ->
                            val curKeyName = getGeneratedName(extraConfig.id, enum.keyType, key)
                            val curValueName = getGeneratedName(extraConfig.id, enum.valType, value)
                            println("        $curKeyName to $curValueName,")
                        }
                        println("    )")
                    } else {
                        println("    val ${extraConfig.name}: Map<$keyType, $valueType> = " +
                            "EnumBlueprints[${extraConfig.id}] as Map<$keyType, $valueType>"
                        )
                    }
                }
                writeEnum(enumsToWrite.removeAt(0))
            }
            println("}")
        }.flush()
    }

    private fun Path.generateSourceIds(name: String, configs: Map<Int, NamedConfig>) {
        val codeFile = resolve("$name.kt").toFile()
        codeFile.createNewFile()
        PrintWriter(codeFile).apply {
            println("/* This file is automatically generated by ${IdentifierGenerator::class.qualifiedName}. */")
            println("package $packageName")
            println()
            println("@Suppress(\"ObjectPropertyName\")")
            println("object $name {")
            for ((id, config) in configs) {
                val identifier = configNameToIdentifier(id, config.name)
                if (identifier.contains("null", ignoreCase = true)) continue
                println("    const val $identifier: Int = $id")
            }
            println("}")
        }.flush()
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
        private const val packageName: String = "io.guthix.oldscape.server.id"

        private val packageDir: String = packageName.replace(".", "/")
    }
}