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

import io.guthix.oldscape.cache.config.NpcConfig
import io.guthix.oldscape.wiki.wikitext.NpcWikiDefinition
import io.ktor.util.KtorExperimentalAPI
import kotlinx.serialization.ImplicitReflectionSerializer
import mu.KotlinLogging
import java.nio.file.Path

private val logger = KotlinLogging.logger {}

@KtorExperimentalAPI
@ImplicitReflectionSerializer
fun main() {
    JsonDownloader(logger).download<NpcConfig, NpcWikiDefinition>(
        Path.of("downloads/npcconfig.json"),
        NpcWikiDefinition,
        NpcConfig
    )
}