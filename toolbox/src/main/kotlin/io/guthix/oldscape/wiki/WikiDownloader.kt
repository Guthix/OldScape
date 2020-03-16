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
import io.guthix.oldscape.cache.config.NamedConfig
import io.guthix.oldscape.cache.config.NamedConfigCompanion
import io.guthix.oldscape.cache.config.ObjectConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import kotlinx.coroutines.*
import mu.KLoggable
import java.nio.file.Path

abstract class WikiDownloader : KLoggable {
    inline fun <reified C : NamedConfig, reified P : WikiDefinition<P>> fromCache(
        configComp: WikiConfigCompanion,
        cacheLoader: NamedConfigCompanion<C>
    ): List<P> {
        val cache = Js5Cache(Js5DiskStore.open(Path.of(WikiDownloader::class.java.getResource("/cache").toURI())))
        val cacheConfigs = cacheLoader.load(cache.readArchive(ConfigArchive.id).readGroup(cacheLoader.id)).values
            .toTypedArray()
        return scrapeWikiConfigs(configComp, *cacheConfigs)
    }

    inline fun <reified P : WikiDefinition<P>>scrapeWikiConfigs(
        configComp: WikiConfigCompanion,
        vararg cacheConfigs: NamedConfig
    )= runBlocking {
        val wikiConfigs = HttpClient(Apache) {
            followRedirects = false
        }.use { client ->
            val wikiConfigs = mutableMapOf<Int, P>()
            for(cacheConfig in cacheConfigs) {
                logger.info { "--------------Handle ${cacheConfig.id} ${cacheConfig.name}----------------" }
                if(cacheConfig is ObjectConfig) {
                    if(cacheConfig.isNoted) {
                        logger.info { "Object ${cacheConfig.id} is noted for obj ${cacheConfig.notedId}" }
                        continue
                    }
                    if(cacheConfig.isPlaceHolder) {
                        logger.info { "Object ${cacheConfig.id} is placeholder for obj ${cacheConfig.placeholderId}" }
                        continue
                    }
                }
                if(!wikiConfigs.containsKey(cacheConfig.id)) {
                    logger.info { "Downloading ${cacheConfig.id} ${cacheConfig.name}" }
                    val wikiText = try {
                        client.scrapeWikiText(configComp.queryString, cacheConfig.id, cacheConfig.name)
                    } catch (e: PageNotFoundException) {
                        logger.warn { e.message }
                        continue
                    }
                    parseWikiString<P>(wikiText).forEach { wikiConfig ->
                        wikiConfig.name = cacheConfig.name
                        val ids = wikiConfig.ids?.toList() ?: listOf(cacheConfig.id)
                        ids.forEach { wikiId ->
                            logger.info { "Downloaded config for name: ${wikiConfig.name} id: $wikiId" }
                            wikiConfigs[wikiId] = wikiConfig
                        }
                    }
                } else {
                    logger.info { "Config ${cacheConfig.id} ${cacheConfig.name} already downloaded" }
                }
            }
            wikiConfigs
        }
        wikiConfigs.values.toList()
    }
}