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

import io.guthix.oldscape.cache.config.Config
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

abstract class ConfigTemplate(private val cacheConfig: Config)

open class ConfigTemplateLoader<C : Config> {
    protected lateinit var templates: Map<Int, C>

    operator fun get(index: Int): C = templates[index]  ?: throw TemplateNotFoundException(index)

    internal fun load(configs: Map<Int, C>) {
        templates = configs
        logger.info { "Loaded ${templates.size} npc templates" }
    }
}