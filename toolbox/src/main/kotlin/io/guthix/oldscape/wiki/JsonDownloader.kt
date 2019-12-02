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
import io.guthix.oldscape.wiki.wikitext.NpcWikiDefinition
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Path
import kotlin.Exception

private val logger = KotlinLogging.logger {}

@ExperimentalCoroutinesApi
@KtorExperimentalAPI
fun main() {
    val npcConfigs = JsonDownloader().load()
    val wikiConfigsJson = Json(JsonConfiguration.Stable.copy(encodeDefaults = false, prettyPrint = true))
        .stringify(NpcWikiDefinition.serializer().list, npcConfigs)
    val path = Path.of(JsonDownloader::class.java.getResource("/").toURI())
    println(path)
    Files.writeString(path.resolve("wikiNpcConfigs.json"), wikiConfigsJson)
}

class JsonDownloader {
    @ExperimentalCoroutinesApi
    @KtorExperimentalAPI
    fun load() = runBlocking<List<NpcWikiDefinition>> {
        val ds = Js5DiskStore.open(Path.of(JsonDownloader::class.java.getResource("/cache").toURI()))
        val cache = Js5Cache(ds)
        val cacheConfigs = NpcConfig.load(cache.readArchive(ConfigArchive.id).readGroup(NpcConfig.id)).values
        val wikiConfigs = mutableListOf<NpcWikiDefinition>()
        val idsScraped = mutableListOf<Int>()
        for(config in cacheConfigs) {
            if(config.id !in idsScraped) {
                val wikiText = requestWikiText(config)
                wikiText ?: logger.error { "Could not retrieve wikitext for id ${config.id} name ${config.name}" }
                val definitions = parseWikiString<NpcWikiDefinition>(wikiText ?: "")
                definitions.forEach { definition ->
                    if(definition.ids == null) {
                        definition.ids = mutableListOf(config.id)
                    }
                    if(definition.name == null) {
                        definition.name = config.name
                    }
                    idsScraped.addAll(definition.ids ?: mutableListOf(config.id))
                }
                wikiConfigs.addAll(definitions)
            } else {
                logger.info { "Already found wiki config for id ${config.id} name ${config.name}" }
            }
        }
        wikiConfigs.sortBy { it.ids?.first() }
        wikiConfigs
    }

    @KtorExperimentalAPI
    suspend fun requestWikiText(config: NpcConfig): String? {
        var wikiText: String? = null
        var tryCount = 0
        while(tryCount <= maxTryCount) {
            wikiText = try {
                withTimeout(timeOutTime) {
                    scrapeWikiText(NpcWikiDefinition.queryString, config.id, config.name)
                }
            } catch (ep: PageNotFoundException) {
                logger.error { "Could not scrape wiki for id ${config.id} name ${config.name}"}
                break
            } catch (e: Exception) {
                logger.error(e) { "Try $tryCount - could not scrape wiki for id ${config.id} name ${config.name}"}
                tryCount++
                continue
            }
            break
        }
        return wikiText
    }

    companion object {
        const val timeOutTime = 10000L
        const val maxConcurrency = 1
        const val maxTryCount = 5
    }
}