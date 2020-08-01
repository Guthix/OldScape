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
import io.guthix.oldscape.cache.config.SequenceConfig
import mu.KotlinLogging
import java.io.IOException

private val logger = KotlinLogging.logger { }

data class SequenceTemplate(val id: Int, val duration: Int?) {
    companion object {
        private lateinit var blueprints: Map<Int, SequenceTemplate>

        operator fun get(index: Int): SequenceTemplate = blueprints[index]
                ?: throw IOException("Could not find sequence $index.")

        fun load(archive: Js5Archive): Map<Int, SequenceTemplate> {
            val configs: Map<Int, SequenceConfig> = SequenceConfig.load(archive.readGroup(SequenceConfig.id))
            blueprints = mutableMapOf<Int, SequenceTemplate>().apply {
                configs.forEach { (id, config) ->
                    put(id, SequenceTemplate(id, config.frameDuration?.sum()?.toDouble()?.div(30)?.toInt()))
                }
            }
            logger.info { "Loaded ${blueprints.size} sequences" }
            return blueprints
        }
    }
}