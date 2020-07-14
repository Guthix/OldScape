/*
 * This file is part of Guthix OldScape-Server.
 *
 * Guthix OldScape-Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Guthix OldScape-Server. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.api

import io.guthix.cache.js5.Js5Archive
import io.guthix.oldscape.cache.config.SequenceConfig
import io.guthix.oldscape.server.blueprints.SequenceBlueprint
import mu.KotlinLogging
import java.io.IOException

private val logger = KotlinLogging.logger { }

object SequenceBlueprints {
    private lateinit var blueprints: Map<Int, SequenceBlueprint>

    operator fun get(index: Int): SequenceBlueprint = blueprints[index]
        ?: throw IOException("Could not find sequence $index.")

    fun load(archive: Js5Archive): Map<Int, SequenceBlueprint> {
        val configs: Map<Int, SequenceConfig> = SequenceConfig.load(archive.readGroup(SequenceConfig.id))
        blueprints = mutableMapOf<Int, SequenceBlueprint>().apply {
            configs.forEach { (id, config) ->
                put(id, SequenceBlueprint(id, config.frameDuration?.sum()?.toDouble()?.div(30)?.toInt()))
            }
        }
        logger.info { "Loaded ${blueprints.size} sequences" }
        return blueprints
    }
}