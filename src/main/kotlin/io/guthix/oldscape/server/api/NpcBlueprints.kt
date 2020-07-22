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