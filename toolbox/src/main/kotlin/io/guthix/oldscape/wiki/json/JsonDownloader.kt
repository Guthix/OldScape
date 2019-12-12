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
package io.guthix.oldscape.wiki.json

import io.guthix.oldscape.cache.config.NamedConfig
import io.guthix.oldscape.cache.config.NamedConfigCompanion
import io.guthix.oldscape.wiki.WikiConfigCompanion
import io.guthix.oldscape.wiki.WikiDefinition
import io.guthix.oldscape.wiki.WikiDownloader
import io.ktor.util.KtorExperimentalAPI
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import kotlinx.serialization.serializer
import mu.KLogger
import java.nio.file.Files
import java.nio.file.Path

class JsonDownloader(override val logger: KLogger) : WikiDownloader() {
    val jsonConfig = JsonConfiguration.Stable.copy(encodeDefaults = false, prettyPrint = true)

    @ImplicitReflectionSerializer
    @KtorExperimentalAPI
    inline fun <reified C : NamedConfig, reified P : WikiDefinition<P>> download(
        to: Path,
        configComp: WikiConfigCompanion,
        cacheLoader: NamedConfigCompanion<C>
    ) {
        val wikiConfigs = fromCache<C, P>(configComp, cacheLoader)
        val wikiConfigsJson = Json(jsonConfig).stringify(P::class.serializer().list, wikiConfigs)
        Files.writeString(to, wikiConfigsJson)
    }
}