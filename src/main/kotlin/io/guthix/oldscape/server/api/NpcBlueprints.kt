/**
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
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.api

import io.guthix.oldscape.cache.config.NpcConfig
import io.guthix.oldscape.server.blueprints.ExtraNpcConfig
import io.guthix.oldscape.server.blueprints.NpcBlueprint
import mu.KotlinLogging
import java.io.IOException

private val logger = KotlinLogging.logger { }

object NpcBlueprints {
    lateinit var blueprints: Map<Int, NpcBlueprint>

    inline operator fun <reified T : NpcBlueprint> get(index: Int): T {
        val bp = blueprints[index] ?: throw IOException("Could not find blueprint $index.")
        if (bp !is T) {
            throw TypeCastException("")
        }
        return bp
    }

    fun load(
        cConfigs: Map<Int, NpcConfig>,
        extraNpcConfigs: List<ExtraNpcConfig>
    ) {
        blueprints = mutableMapOf<Int, NpcBlueprint>().apply {
            addBlueprints(cConfigs, extraNpcConfigs, ::NpcBlueprint)
        }
        logger.info { "Loaded ${blueprints.size} npc blueprints" }
    }

    private fun <E : ExtraNpcConfig, B : NpcBlueprint> MutableMap<Int, NpcBlueprint>.addBlueprints(
        cacheConfigs: Map<Int, NpcConfig>,
        extraObjectConfigs: List<E>,
        construct: (NpcConfig, E) -> B
    ) {
        extraObjectConfigs.forEach { extraConfig ->
            extraConfig.ids.forEach inner@{ id ->
                val cacheConfig = cacheConfigs[id] ?: kotlin.run {
                    logger.warn { "Could not find npc for id $id." }
                    return@inner
                }
                put(id, construct.invoke(cacheConfig, extraConfig))
            }
        }
    }
}