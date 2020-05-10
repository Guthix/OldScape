/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
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

private val logger = KotlinLogging.logger {  }

object NpcBlueprints {
    private lateinit var blueprints: Map<Int, NpcBlueprint>

    operator fun get(index: Int): NpcBlueprint {
        return blueprints[index] ?: throw IOException("Could not find blueprint $index.")
    }

    fun load(cacheConfigs: Map<Int, NpcConfig>, extraObjConfigs: List<ExtraNpcConfig>) {
        val bps = mutableMapOf<Int, NpcBlueprint>()
        extraObjConfigs.forEach { extraConfig ->
            extraConfig.ids.forEach {  id ->
                val cacheConfig = cacheConfigs[id] ?: error("Extra config for id $id is not found in the cache.")
                bps[id] = NpcBlueprint(
                    cacheConfig.id,
                    cacheConfig.name,
                    extraConfig.examine,
                    cacheConfig.size.toInt(),
                    cacheConfig.combatLevel,
                    cacheConfig.isInteractable,
                    cacheConfig.walkSequence,
                    cacheConfig.walkLeftSequence,
                    cacheConfig.walkRightSequence,
                    cacheConfig.walkBackSequence,
                    cacheConfig.turnLeftSequence,
                    cacheConfig.turnRightSequence,
                    extraConfig.combat
                )
            }
        }
        blueprints = bps.toMap()
        logger.info { "Loaded ${blueprints.size} npc blueprints" }
    }
}