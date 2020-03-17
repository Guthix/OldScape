/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.wiki

import io.guthix.cache.js5.Js5Cache
import io.guthix.cache.js5.container.disk.Js5DiskStore
import io.guthix.oldscape.cache.ConfigArchive
import io.guthix.oldscape.cache.config.NpcConfig
import io.guthix.oldscape.cache.config.ObjectConfig
import io.guthix.oldscape.wiki.wikitext.NpcWikiDefinition
import io.guthix.oldscape.wiki.wikitext.ObjectWikiDefinition
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import kotlinx.coroutines.*
import mu.KotlinLogging
import java.nio.file.Path

private val logger = KotlinLogging.logger {  }

fun npcWikiDownloader(cachePath: Path): List<NpcWikiDefinition> {
    val cache = Js5Cache(Js5DiskStore.open(cachePath))
    val configArchive = cache.readArchive(ConfigArchive.id)
    val cacheConfigs = NpcConfig.load(configArchive.readGroup(NpcConfig.id))
    return scrapeNpcWikiConfigs(cacheConfigs)
}

fun objectWikiDownloader(cachePath: Path): List<ObjectWikiDefinition> {
    val cache = Js5Cache(Js5DiskStore.open(cachePath))
    val configArchive = cache.readArchive(ConfigArchive.id)
    val cacheConfigs = ObjectConfig.load(configArchive.readGroup(ObjectConfig.id))
    return scrapeObjectWikiConfigs(cacheConfigs)
}

fun scrapeObjectWikiConfigs(cacheConfigs: Map<Int, ObjectConfig>)= runBlocking {
    val wikiConfigs = HttpClient(Apache) {
        followRedirects = false
    }.use { client ->
        val wikiConfigs = mutableMapOf<Int, ObjectWikiDefinition>()
        for((id, cacheConfig) in cacheConfigs) {
            logger.info { "--------------Handle $id ${cacheConfig.name}----------------" }
            if(cacheConfig.isNoted) {
                logger.info { "Object $id is noted for obj ${cacheConfig.notedId}" }
                continue
            }
            if(cacheConfig.isPlaceHolder) {
                logger.info { "Object $id is placeholder for obj ${cacheConfig.placeholderId}" }
                continue
            }
            if(!wikiConfigs.containsKey(id)) {
                logger.info { "Downloading $id ${cacheConfig.name}" }
                val wikiText = try {
                    client.scrapeWikiText(ObjectWikiDefinition.queryString, id, cacheConfig.name)
                } catch (e: PageNotFoundException) {
                    logger.warn { e.message }
                    continue
                }
                parseWikiString<ObjectWikiDefinition>(wikiText).forEach { wikiConfig ->
                    wikiConfig.name = cacheConfig.name
                    val ids = wikiConfig.ids?.toList() ?: listOf(id)
                    ids.forEach { wikiId ->
                        logger.info { "Downloaded config for name: ${wikiConfig.name} id: $wikiId" }
                        wikiConfigs[wikiId] = wikiConfig
                    }
                }
            } else {
                logger.info { "Config $id ${cacheConfig.name} already downloaded" }
            }
        }
        wikiConfigs
    }
    wikiConfigs.values.toList()
}

fun scrapeNpcWikiConfigs(cacheConfigs: Map<Int, NpcConfig>)= runBlocking {
    val wikiConfigs = HttpClient(Apache) {
        followRedirects = false
    }.use { client ->
        val wikiConfigs = mutableMapOf<Int, NpcWikiDefinition>()
        for((id, cacheConfig) in cacheConfigs) {
            logger.info { "--------------Handle $id ${cacheConfig.name}----------------" }
            if(!wikiConfigs.containsKey(id)) {
                logger.info { "Downloading $id ${cacheConfig.name}" }
                val wikiText = try {
                    client.scrapeWikiText(NpcWikiDefinition.queryString, id, cacheConfig.name)
                } catch (e: PageNotFoundException) {
                    logger.warn { e.message }
                    continue
                }
                parseWikiString<NpcWikiDefinition>(wikiText).forEach { wikiConfig ->
                    wikiConfig.name = cacheConfig.name
                    val ids = wikiConfig.ids?.toList() ?: listOf(id)
                    ids.forEach { wikiId ->
                        logger.info { "Downloaded config for name: ${wikiConfig.name} id: $wikiId" }
                        wikiConfigs[wikiId] = wikiConfig
                    }
                }
            } else {
                logger.info { "Config $id ${cacheConfig.name} already downloaded" }
            }
        }
        wikiConfigs
    }
    wikiConfigs.values.toList()
}