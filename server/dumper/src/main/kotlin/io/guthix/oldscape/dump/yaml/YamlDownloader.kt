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
package io.guthix.oldscape.dump.yaml

import com.charleskorn.kaml.Yaml
import io.guthix.js5.Js5Cache
import io.guthix.js5.container.disk.Js5DiskStore
import io.guthix.oldscape.cache.ConfigArchive
import io.guthix.oldscape.cache.config.NpcConfig
import io.guthix.oldscape.cache.config.ObjConfig
import io.guthix.oldscape.server.readYaml
import io.guthix.oldscape.server.template.Template
import io.guthix.oldscape.wiki.WikiDefinition
import io.guthix.oldscape.wiki.scrapeNpcWikiConfigs
import io.guthix.oldscape.wiki.scrapeObjectWikiConfigs
import io.guthix.oldscape.wiki.wikitext.NpcWikiDefinition
import io.guthix.oldscape.wiki.wikitext.ObjWikiDefinition
import kotlinx.serialization.encodeToString
import mu.KotlinLogging
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

private val logger = KotlinLogging.logger { }

fun main(args: Array<String>) {
    YamlDownloader.main(args)
}

object YamlDownloader {
    private val cachePath = Path.of(javaClass.getResource("/cache").toURI())

    private val serverPath = Path.of(javaClass.getResource("/server").toURI())

    private val dumpPath = File("dump").toPath()

    @JvmStatic
    fun main(args: Array<String>) {
        val cache = Js5Cache(Js5DiskStore.open(cachePath))
        val configArchive = cache.readArchive(ConfigArchive.id)

        val npcCacheConfigs = NpcConfig.load(configArchive.readGroup(NpcConfig.id))
        val objCacheConfigs = ObjConfig.load(configArchive.readGroup(ObjConfig.id))

        val objWikiConfigs = scrapeObjectWikiConfigs(objCacheConfigs).filter { it.ids != null }.sortedBy {
            it.ids!!.first()
        }
        val npcWikiConfigs = scrapeNpcWikiConfigs(npcCacheConfigs).filter { it.ids != null }.sortedBy {
            it.ids!!.first()
        }

        // TODO remove ids that are not in the cache
        val equipmentDefs = objWikiConfigs.filter { it.isEquipable == true }
        writeTemplate(equipmentDefs, "Equipment.yaml", ObjWikiDefinition::toEquipmentTemplate)

        val weaponDefs = objWikiConfigs.filter { it.combatStyle != null }
        writeTemplate(weaponDefs, "Weapons.yaml", ObjWikiDefinition::toWeaponTemplate)

        val weightDefs = objWikiConfigs.filter { it.weight != null }
        writeTemplate(weightDefs, "ObjWeights.yaml", ObjWikiDefinition::toWeightTemplate)

        val monsterDefs = npcWikiConfigs.filter { it.combatLvl != null }
        writeTemplate(monsterDefs, "Monsters.yaml", NpcWikiDefinition::toMonsterTemplate)
    }

    fun <D : WikiDefinition, T : Template> writeTemplate(
        defs: List<D>,
        fileName: String,
        templateBuilder: D.(T?) -> T
    ) {
        val serverTemplates = try {
            Yaml.default.readYaml<List<T>>(Files.readString(serverPath.resolve(fileName)))
        } catch (e: Exception) {
            emptyList()
        }
        val dumpFile = dumpPath.resolve(fileName)
        val yamlString = Yaml.default.encodeToString(defs.map { dump ->
            val serverTemplate = serverTemplates.find { dump.ids == it.ids }
            dump.templateBuilder(serverTemplate)
        })
        Files.writeString(dumpFile, yamlString)
        logger.info { "Written ${defs.size} templates to ${dumpFile.toFile().absolutePath}" }
    }
}