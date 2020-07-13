/*
 * This file is part of Guthix OldScape-Wiki.
 *
 * Guthix OldScape-Wiki is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Wiki is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Guthix OldScape-Wiki. If not, see <https://www.gnu.org/licenses/>.
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
import io.ktor.client.request.get
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import java.nio.file.Path

private const val wikiUrl = "https://oldschool.runescape.wiki"

private val logger = KotlinLogging.logger { }

public fun npcWikiDownloader(cachePath: Path): List<NpcWikiDefinition> {
    val cache = Js5Cache(Js5DiskStore.open(cachePath))
    val configArchive = cache.readArchive(ConfigArchive.id)
    val cacheConfigs = NpcConfig.load(configArchive.readGroup(NpcConfig.id))
    return scrapeNpcWikiConfigs(cacheConfigs)
}

public fun objectWikiDownloader(cachePath: Path): List<ObjectWikiDefinition> {
    val cache = Js5Cache(Js5DiskStore.open(cachePath))
    val configArchive = cache.readArchive(ConfigArchive.id)
    val cacheConfigs = ObjectConfig.load(configArchive.readGroup(ObjectConfig.id))
    return scrapeObjectWikiConfigs(cacheConfigs)
}

public fun scrapeObjectWikiConfigs(cacheConfigs: Map<Int, ObjectConfig>): List<ObjectWikiDefinition> = runBlocking {
    val wikiConfigs = HttpClient(Apache) {
        followRedirects = false
        engine {
            socketTimeout = 200_000
            connectTimeout = 200_000
            connectionRequestTimeout = 400_000
        }
    }.use { client ->
        val wikiConfigs = mutableMapOf<Int, ObjectWikiDefinition>()
        for ((id, cacheConfig) in cacheConfigs) {
            logger.info { "--------------Handle $id ${cacheConfig.name}----------------" }
            if (cacheConfig.isNoted) {
                logger.info { "Object $id is noted for obj ${cacheConfig.notedId}" }
                continue
            }
            if (cacheConfig.isPlaceHolder) {
                logger.info { "Object $id is placeholder for obj ${cacheConfig.placeholderId}" }
                continue
            }
            if (!wikiConfigs.containsKey(id)) {
                logger.info { "Downloading $id ${cacheConfig.name}" }
                val wikiText = try {
                    client.scrapeWikiText(ObjectWikiDefinition.queryString, id, cacheConfig.name)
                } catch (e: PageNotFoundException) {
                    logger.warn(e::message)
                    continue
                }

                inner@ for (entry in wikiText.split("(?=Infobox Item)", ignoreCase = true)) {
                    if (!wikiText.contains("Infobox Item", ignoreCase = true)) { // remove wrong queries
                        logger.info { "Scraped page for object $id is not an object" }
                        continue@inner
                    }
                    parseWikiString<ObjectWikiDefinition>(entry).forEach { wikiConfig ->
                        wikiConfig.name = cacheConfig.name
                        val ids = wikiConfig.ids?.toList() ?: listOf(id)
                        ids.forEach { wikiId ->
                            logger.info { "Downloaded config for name: ${wikiConfig.name} id: $wikiId" }
                            wikiConfigs[wikiId] = wikiConfig
                        }
                    }
                }
            } else {
                logger.info { "Config $id ${cacheConfig.name} already downloaded" }
            }
        }
        wikiConfigs
    }
    wikiConfigs.values.distinct()
}

public fun scrapeNpcWikiConfigs(cacheConfigs: Map<Int, NpcConfig>): List<NpcWikiDefinition> = runBlocking {
    val wikiConfigs = HttpClient(Apache) {
        followRedirects = false
    }.use { client ->
        val wikiConfigs = mutableMapOf<Int, NpcWikiDefinition>()
        for ((id, cacheConfig) in cacheConfigs) {
            logger.info { "--------------Handle $id ${cacheConfig.name}----------------" }
            if (!wikiConfigs.containsKey(id)) {
                logger.info { "Downloading $id ${cacheConfig.name}" }
                val wikiText = try {
                    client.scrapeWikiText(NpcWikiDefinition.queryString, id, cacheConfig.name)
                } catch (e: PageNotFoundException) {
                    logger.warn(e::message)
                    continue
                }

                inner@ for (entry in wikiText.split("(?=Infobox NPC)", "(?=Infobox Monster)", ignoreCase = true)) {
                    if (!wikiText.contains("Infobox NPC", ignoreCase = true) &&
                        !wikiText.contains("Infobox Monster", ignoreCase = true)
                    ) { // remove wrong queries
                        logger.info { "Scraped page for npc $id is not a npc" }
                        continue@inner
                    }
                    parseWikiString<NpcWikiDefinition>(entry).forEach { wikiConfig ->
                        wikiConfig.name = cacheConfig.name
                        val ids = wikiConfig.ids?.toList() ?: listOf(id)
                        ids.forEach { wikiId ->
                            logger.info { "Downloaded config for name: ${wikiConfig.name} id: $wikiId" }
                            wikiConfigs[wikiId] = wikiConfig
                        }
                    }
                }

            } else {
                logger.info { "Config $id ${cacheConfig.name} already downloaded" }
            }
        }
        wikiConfigs
    }
    wikiConfigs.values.distinct()
}

/** Scrapes the wiki and retrieves the wiki text.*/
public suspend fun HttpClient.scrapeWikiText(wikiType: String, id: Int, name: String): String {
    val urlName = name.replace(' ', '_').replace("<.*?>".toRegex(), "")
    val queryUrl = if (urlName.contains("%")) {
        "$wikiUrl/w/Special:Lookup?type=$wikiType&id=$id"
    } else {
        "$wikiUrl/w/Special:Lookup?type=$wikiType&id=$id&name=$urlName"
    }
    val redirect = request<HttpResponse>(queryUrl).call.response.headers["location"]
        ?: throw PageNotFoundException("Could not retrieve redirect for $queryUrl.")
    val dir = redirect.substringAfter("oldschool.runescape.wiki/")
    if (dir.startsWith("w/Null") || dir.startsWith("w/Special:Search")) {
        throw PageNotFoundException("Could not retrieve redirect for $queryUrl.")
    }
    val rawUrl = "${redirect.substringBefore("#")}?action=raw"
    return get(rawUrl)
}

public class PageNotFoundException(message: String) : Exception(message)