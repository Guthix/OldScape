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

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.guthix.js5.Js5Cache
import io.guthix.js5.container.disk.Js5DiskStore
import io.guthix.oldscape.cache.ConfigArchive
import io.guthix.oldscape.cache.config.NpcConfig
import io.guthix.oldscape.cache.config.ObjectConfig
import io.guthix.oldscape.server.template.Template
import io.guthix.oldscape.wiki.WikiDefinition
import io.guthix.oldscape.wiki.scrapeNpcWikiConfigs
import io.guthix.oldscape.wiki.scrapeObjectWikiConfigs
import io.guthix.oldscape.wiki.wikitext.NpcWikiDefinition
import io.guthix.oldscape.wiki.wikitext.ObjWikiDefinition
import mu.KotlinLogging
import java.io.File
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
        val yamlFactory = YAMLFactory()
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
        val objectMapper = ObjectMapper(yamlFactory)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .registerKotlinModule()

        val cache = Js5Cache(Js5DiskStore.open(cachePath))
        val configArchive = cache.readArchive(ConfigArchive.id)

        val npcCacheConfigs = NpcConfig.load(configArchive.readGroup(NpcConfig.id))
        val objCacheConfigs = ObjectConfig.load(configArchive.readGroup(ObjectConfig.id))

        val objWikiConfigs = scrapeObjectWikiConfigs(objCacheConfigs).filter { it.ids != null }.sortedBy {
            it.ids!!.first()
        }
        val npcWikiConfigs = scrapeNpcWikiConfigs(npcCacheConfigs).filter { it.ids != null }.sortedBy {
            it.ids!!.first()
        }

        // TODO remove ids that are not in the cache

        val writer = objectMapper.writer(
            DefaultPrettyPrinter().withObjectIndenter(DefaultIndenter().withLinefeed("\r\n"))
        )
        val equipmentDefs = objWikiConfigs.filter { it.isEquipable == true }
        objectMapper.writeTemplate(writer, equipmentDefs, "Equipment.yaml", ObjWikiDefinition::toEquipmentTemplate)

        val weaponDefs = objWikiConfigs.filter { it.combatStyle != null }
        objectMapper.writeTemplate(writer, weaponDefs, "Weapons.yaml", ObjWikiDefinition::toWeaponTemplate)

        val weightDefs = objWikiConfigs.filter { it.weight != null }
        objectMapper.writeTemplate(writer, weightDefs, "ObjWeights.yaml", ObjWikiDefinition::toWeightTemplate)

        val monsterDefs = npcWikiConfigs.filter { it.combatLvl != null }
        objectMapper.writeTemplate(writer, monsterDefs, "Monsters.yaml", NpcWikiDefinition::toMonsterTemplate)
    }

    fun <D : WikiDefinition, T : Template> ObjectMapper.writeTemplate(
        writer: ObjectWriter,
        defs: List<D>,
        fileName: String,
        templateBuilder: D.(T?) -> T
    ) {
        val serverTemplates = try {
            readValue<List<T>>(serverPath.resolve(fileName).toFile())
        } catch (e: Exception) {
            emptyList()
        }
        val dumpFile = dumpPath.resolve(fileName).toFile()
        writer.writeValue(dumpFile, defs.map { dump ->
            val serverTemplate = serverTemplates.find { dump.ids == it.ids }
            dump.templateBuilder(serverTemplate)
        })
        logger.info { "Written ${defs.size} templates to ${dumpFile.absoluteFile.absolutePath}" }
    }
}