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
package io.guthix.oldscape.server.world.entity

import io.guthix.cache.js5.Js5Archive
import io.guthix.oldscape.cache.config.SpotAnimConfig
import io.guthix.oldscape.server.template.SpotAnimTemplate
import mu.KotlinLogging
import java.io.IOException

private val logger = KotlinLogging.logger { }

data class SpotAnimation(
    private val template: SpotAnimTemplate,
    val height: Int,
    val delay: Int = 0,
) {
    val id: Int get() = template.id

    val duration: Int? get() = template.duration

    companion object {
        private lateinit var templates: Map<Int, SpotAnimTemplate>

        operator fun get(index: Int): SpotAnimTemplate = templates[index] ?: throw IOException(
                "Could not find spot animation $index."
        )

        fun loadTemplates(archive: Js5Archive): Map<Int, SpotAnimTemplate> {
            val configs = SpotAnimConfig.load(archive.readGroup(SpotAnimConfig.id))
            val loadedTemplates = mutableMapOf<Int, SpotAnimTemplate>()
            configs.forEach { (id, config) ->
                val sequence = Sequence[config.sequenceId ?: throw IOException(
                    "Could not find sequence ${config.sequenceId}"
                )]
                loadedTemplates[id] = SpotAnimTemplate(id, sequence.duration)
            }
            logger.info { "Loaded ${configs.size} spot animations" }
            return loadedTemplates
        }
    }
}