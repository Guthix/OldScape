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

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.guthix.js5.Js5Cache
import io.guthix.js5.container.disk.Js5DiskStore
import io.guthix.oldscape.cache.ConfigArchive
import io.guthix.oldscape.cache.config.LocationConfig
import io.guthix.oldscape.cache.config.NpcConfig
import io.guthix.oldscape.cache.config.ObjectConfig
import io.guthix.oldscape.codegen.writeIntTemplates
import io.guthix.oldscape.codegen.writeNamedConfigTemplates
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import java.io.File
import java.io.PrintWriter
import java.nio.file.Path

class IdGenerator : CodeGenerator() {
    override fun apply(target: Project) {
        super.apply(target)
        val idGenTask = target.task("generateCacheIds") {
            doFirst {
                val cacheDir = "${target.rootProject.buildDir}/cache"
                Js5Cache(Js5DiskStore.open(File(cacheDir).toPath())).use { cache ->
                    val configArchive = cache.readArchive(ConfigArchive.id)

                    val locs = LocationConfig.load(configArchive.readGroup(LocationConfig.id))
                    target.writeNamedConfigTemplates("Loc", locs, true)

                    val objs = ObjectConfig.load(configArchive.readGroup(ObjectConfig.id))
                    target.writeNamedConfigTemplates("Obj", objs, false)

                    val npcs = NpcConfig.load(configArchive.readGroup(NpcConfig.id))
                    target.writeNamedConfigTemplates("Npc", npcs, false)

                    target.writeIntTemplates("Enums", enumKtFileName)
                    target.writeIntTemplates("SpotAnims", spotAnimKtFileName)
                    target.writeIntTemplates("Inventories", invKtFileName)
                    target.writeIntTemplates("Sequences", sequenceKtFileName)
                    target.writeIntTemplates("Varps", varpKtFileName)
                    target.writeIntTemplates("Varbits", varbitKtFileName)
                    target.writeIntTemplates("CS2s", cs2KtFileName)
                    target.writeIntTemplates("MusicTracks", musicTrackKtFileName)
                    target.writeIntTemplates("ObjParams", objParamFileName)
                    target.writeIntTemplates("NpcParams", npcParamFileName)
                }
            }
        }
        val classesTask = target.tasks.getByName("compileKotlin")
        idGenTask.group = BasePlugin.BUILD_GROUP
        idGenTask.dependsOn(target.rootProject.tasks.getByName("unzipCache"))
        classesTask.dependsOn(idGenTask)
    }

    companion object {
        const val packageName: String = "io.guthix.oldscape.cache"

        const val cacheDir: String = "cache/src/main/resources"
        const val nameDir: String = "cache/names/src/main/resources"

        const val enumKtFileName: String = "EnumId"
        const val spotAnimKtFileName: String = "SpotAnimId"
        const val invKtFileName: String = "InventoryId"
        const val sequenceKtFileName: String = "SequenceId"
        const val varpKtFileName: String = "VarpId"
        const val varbitKtFileName: String = "VarbitId"
        const val cs2KtFileName: String = "CS2Id"
        const val musicTrackKtFileName: String = "MusicTrackId"
        const val locParamFileName: String = "LocParamId"
        const val objParamFileName: String = "ObjParamId"
        const val npcParamFileName: String = "NpcParamId"
    }
}

fun createSourceTree(target: Project): Path {
    val srcDir = File("${target.projectDir}/${CodeGenerator.generatedFolder}")
    srcDir.mkdirs()
    return srcDir.toPath()
}

fun configNameToIdentifier(id: Int, name: String): String {
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

    val normalizedName = name.toUpperCase().replace(' ', '_').replace(Regex("[^a-zA-Z\\d_:]"), "").removeTags()
    val propName = if (normalizedName.isNotEmpty()) normalizedName + "_$id" else "$id"
    return if (propName.first().isDigit()) "`$propName`" else propName
}

data class NamedId(val id: Int, val name: String)

fun Project.readNamedIds(name: String): List<NamedId> {
    return ObjectMapper(YAMLFactory()).registerKotlinModule()
        .readValue(
            File("$rootDir/${IdGenerator.nameDir}").toPath().resolve("$name.yaml").toFile(),
            object : TypeReference<List<NamedId>>() {}
        )
}

fun PrintWriter.printFileHeader(pkg: String, vararg supressions: String) {
    println(CodeGenerator.warningHeader)
    print("@file:Suppress(\"PropertyName\", \"ObjectPropertyName\"")
    supressions.forEach { print(", \"$it\"") }
    println(")")
    println()
    println("package $pkg")
}

