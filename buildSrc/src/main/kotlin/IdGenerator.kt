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
import java.util.Locale

class IdGenerator : CodeGenerator() {
    override fun apply(target: Project) {
        super.apply(target)
        val idGenTask = target.task("generateCacheIds") {
            doFirst {
                val cacheDir = "${target.rootProject.layout.buildDirectory}/cache"
                Js5Cache(Js5DiskStore.open(File(cacheDir).toPath())).use { cache ->
                    val configArchive = cache.readArchive(ConfigArchive.id)

                    val locs = LocationConfig.load(configArchive.readGroup(LocationConfig.id))
                    target.writeNamedConfigTemplates("Loc", locs, true)

                    val objs = ObjectConfig.load(configArchive.readGroup(ObjectConfig.id))
                    target.writeNamedConfigTemplates("Obj", objs, false)

                    val npcs = NpcConfig.load(configArchive.readGroup(NpcConfig.id))
                    target.writeNamedConfigTemplates("Npc", npcs, false)

                    target.writeIntTemplates("Enums", ENUM_FILE_NAME)
                    target.writeIntTemplates("SpotAnims", SPOT_ANIM_FILE_NAME)
                    target.writeIntTemplates("Inventories", INV_FILE_NAME)
                    target.writeIntTemplates("Sequences", SEQUENCE_FILE_NAME)
                    target.writeIntTemplates("Varps", VARP_FILE_NAME)
                    target.writeIntTemplates("Varbits", VARBIT_FILE_NAME)
                    target.writeIntTemplates("CS2s", CS2_FILE_NAME)
                    target.writeIntTemplates("MusicTracks", MUSIC_TRACK_FILE_NAME)
                    target.writeIntTemplates("ObjParams", OBJ_PARAM_FILE_NAME)
                    target.writeIntTemplates("NpcParams", NPC_PARAM_FILE_NAME)
                }
            }
        }
        val classesTask = target.tasks.getByName("compileKotlin")
        idGenTask.group = BasePlugin.BUILD_GROUP
        idGenTask.dependsOn(target.rootProject.tasks.getByName("unzipCache"))
        classesTask.dependsOn(idGenTask)
    }

    companion object {
        const val PACKAGE_NAME: String = "io.guthix.oldscape.cache"

        const val CACHE_DIR: String = "cache/src/main/resources"
        const val NAME_DIR: String = "cache/names/src/main/resources"

        const val ENUM_FILE_NAME: String = "EnumId"
        const val SPOT_ANIM_FILE_NAME: String = "SpotAnimId"
        const val INV_FILE_NAME: String = "InventoryId"
        const val SEQUENCE_FILE_NAME: String = "SequenceId"
        const val VARP_FILE_NAME: String = "VarpId"
        const val VARBIT_FILE_NAME: String = "VarbitId"
        const val CS2_FILE_NAME: String = "CS2Id"
        const val MUSIC_TRACK_FILE_NAME: String = "MusicTrackId"
        const val LOC_PARAM_FILE_NAME: String = "LocParamId"
        const val OBJ_PARAM_FILE_NAME: String = "ObjParamId"
        const val NPC_PARAM_FILE_NAME: String = "NpcParamId"
    }
}

fun createSourceTree(target: Project): Path {
    val srcDir = File("${target.projectDir}/${CodeGenerator.GENERATED_FOLDER}")
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

    val normalizedName = name
        .uppercase(Locale.getDefault())
        .replace(' ', '_')
        .replace(Regex("[^a-zA-Z\\d_:]"), "")
        .removeTags()
    val propName = if (normalizedName.isNotEmpty()) normalizedName + "_$id" else "$id"
    return if (propName.first().isDigit()) "`$propName`" else propName
}

data class NamedId(val id: Int, val name: String)

fun Project.readNamedIds(name: String): List<NamedId> {
    return ObjectMapper(YAMLFactory()).registerKotlinModule()
        .readValue(
            File("$rootDir/${IdGenerator.NAME_DIR}").toPath().resolve("$name.yaml").toFile(),
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

