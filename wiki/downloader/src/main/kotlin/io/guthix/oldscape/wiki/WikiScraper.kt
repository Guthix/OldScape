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
package io.guthix.oldscape.wiki

import io.guthix.oldscape.cache.config.NpcConfig
import io.guthix.oldscape.cache.config.ObjConfig
import io.guthix.oldscape.wiki.wikitext.NpcWikiDefinition
import io.guthix.oldscape.wiki.wikitext.ObjWikiDefinition
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

private const val wikiUrl = "https://oldschool.runescape.wiki"

private val logger = KotlinLogging.logger { }

fun scrapeObjectWikiConfigs(cacheConfigs: Map<Int, ObjConfig>): List<ObjWikiDefinition> = runBlocking {
    val wikiConfigs = HttpClient(Apache) {
        followRedirects = false
        engine {
            socketTimeout = 200_000
            connectTimeout = 200_000
            connectionRequestTimeout = 400_000
        }
    }.use { client ->
        val cacheIds = cacheConfigs.values.map(ObjConfig::id)
        val wikiConfigs = mutableMapOf<Int, ObjWikiDefinition>()
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
                    client.scrapeWikiText(ObjWikiDefinition.queryString, id, cacheConfig.name)
                } catch (e: PageNotFoundException) {
                    logger.warn(e::message)
                    continue
                }

                inner@ for (entry in wikiText.split("(?=Infobox Item)", ignoreCase = true)) {
                    if (!wikiText.contains("Infobox Item", ignoreCase = true)) { // remove wrong queries
                        logger.info { "Scraped page for object $id is not an object" }
                        continue@inner
                    }
                    parseWikiString<ObjWikiDefinition>(entry).forEach { wikiConfig ->
                        wikiConfig.name = cacheConfig.name
                        val ids = wikiConfig.ids?.toList() ?: listOf(id)
                        wikiLoop@ for (wikiId in ids) {
                            if (!cacheIds.contains(wikiId)) continue@wikiLoop
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

fun scrapeNpcWikiConfigs(cacheConfigs: Map<Int, NpcConfig>): List<NpcWikiDefinition> = runBlocking {
    val wikiConfigs = HttpClient(Apache) {
        followRedirects = false
    }.use { client ->
        val cacheIds = cacheConfigs.values.map(NpcConfig::id)
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
                        wikiLoop@ for (wikiId in ids) {
                            if (!cacheIds.contains(wikiId)) continue@wikiLoop
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
suspend fun HttpClient.scrapeWikiText(wikiType: String, id: Int, name: String): String {
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

class PageNotFoundException(message: String) : Exception(message)