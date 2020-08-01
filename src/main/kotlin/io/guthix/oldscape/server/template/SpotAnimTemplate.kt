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
package io.guthix.oldscape.server.template

import io.guthix.cache.js5.Js5Archive
import io.guthix.oldscape.cache.config.SpotAnimConfig
import mu.KotlinLogging
import java.io.IOException

private val logger = KotlinLogging.logger { }

data class SpotAnimTemplate(val id: Int, val height: Int, val delay: Int = 0, val sequenceTemplate: SequenceTemplate) {
    companion object {
        private lateinit var configs: Map<Int, SpotAnimConfig>

        operator fun get(index: Int): SpotAnimConfig = configs[index] ?: throw IOException(
            "Could not find spot animation $index."
        )

        fun load(archive: Js5Archive) {
            configs = SpotAnimConfig.load(archive.readGroup(SpotAnimConfig.id))
            logger.info { "Loaded ${configs.size} spot animations" }
        }
    }
}