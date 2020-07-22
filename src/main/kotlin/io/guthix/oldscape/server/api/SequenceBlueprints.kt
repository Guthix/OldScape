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