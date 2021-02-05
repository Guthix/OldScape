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
package io.guthix.oldscape.dump.yaml

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.SequenceStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import io.guthix.js5.Js5Cache
import io.guthix.js5.container.disk.Js5DiskStore
import io.guthix.oldscape.cache.ConfigArchive
import io.guthix.oldscape.cache.config.NpcConfig
import io.guthix.oldscape.cache.config.ObjConfig
import io.guthix.oldscape.server.template.Template
import io.guthix.oldscape.wiki.WikiDefinition
import io.guthix.oldscape.wiki.scrapeNpcWikiConfigs
import io.guthix.oldscape.wiki.scrapeObjectWikiConfigs
import io.guthix.oldscape.wiki.wikitext.NpcWikiDefinition
import io.guthix.oldscape.wiki.wikitext.ObjWikiDefinition
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Path

fun main(args: Array<String>) {
    YamlDownloader.main(args)
}

object YamlDownloader {
    val cachePath: Path = Path.of("server/src/main/resources/cache")

    val serverPath: Path = Path.of("server/src/main/resources/template")

    val dumpPath: Path = Path.of("server/wiki/src/main/resources/dump")

    val yaml: Yaml = Yaml(
        configuration = YamlConfiguration(
            encodeDefaults = false,
            sequenceStyle = SequenceStyle.Flow,
            polymorphismStyle = PolymorphismStyle.Property
        )
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val cache = Js5Cache(Js5DiskStore.open(cachePath))
        val configArchive = cache.readArchive(ConfigArchive.id)

        val npcCacheConfigs = NpcConfig.load(configArchive.readGroup(NpcConfig.id))
        val objCacheConfigs = ObjConfig.load(configArchive.readGroup(ObjConfig.id)).filter { it.key < 0 }

        val npcWikiConfigs = scrapeNpcWikiConfigs(npcCacheConfigs).filter { it.ids != null }.sortedBy {
            it.ids!!.first()
        }
        val objWikiConfigs = scrapeObjectWikiConfigs(objCacheConfigs).filter { it.ids != null }.sortedBy {
            it.ids!!.first()
        }

        val equipmentDefs = objWikiConfigs.filter { it.isEquipable == true }
        writeTemplate(equipmentDefs,
            "server/plugins/core/equipment/src/main/resources/template", "Equipment.yaml",
            ObjWikiDefinition::toEquipmentTemplate
        )

        val weaponDefs = objWikiConfigs.filter { it.combatStyle != null }
        writeTemplate(weaponDefs,
            "server/plugins/core/combat/src/main/resources/template", "Attack.yaml",
            ObjWikiDefinition::toWeaponTemplate
        )

        val weightDefs = objWikiConfigs.filter { it.weight != null }
        writeTemplate(weightDefs,
            "server/plugins/core/obj/src/main/resources/template", "ObjWeights.yaml",
            ObjWikiDefinition::toWeightTemplate
        )

        val monsterDefs = npcWikiConfigs.filter { it.combatLvl != null }
        writeTemplate(
            monsterDefs,
            "server/plugins/core/monster/src/main/resources/template", "Monsters.yaml",
            NpcWikiDefinition::toMonsterTemplate
        )
    }

    inline fun <reified D : WikiDefinition, reified T : Template> writeTemplate(
        defs: List<D>,
        filePath: String,
        fileName: String,
        templateBuilder: D.(T?) -> T
    ) {
        val logger = KotlinLogging.logger { }
        val serverTemplates = try {
            val yamlString: String = Files.readString(Path.of(filePath).resolve(fileName))
            yaml.decodeFromString<Map<String, T>>(yamlString)
        } catch (e: Exception) {
            logger.error { "Count not find ${Path.of(filePath).resolve(fileName).toAbsolutePath()}." }
            emptyMap()
        }
        val dumpFile = dumpPath.resolve(fileName)
        val data: Map<String, T> = defs.map { dump ->
            val serverTemplate = serverTemplates.values.find { dump.ids == it.ids }
            configNameToIdentifier(dump.ids?.first() ?: 0, dump.name ?: "NULL") to dump.templateBuilder(serverTemplate)
        }.toMap()
        val yamlString = yaml.encodeToString(data)
        Files.writeString(dumpFile, yamlString)
        logger.info { "Written ${defs.size} templates to ${dumpFile.toFile().absolutePath}" }
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
}

