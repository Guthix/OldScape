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
import io.guthix.oldscape.cache.config.NamedConfig
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

abstract class Template(open val ids: List<Int>)

abstract class EngineConfigTemplate(
    cacheConfig: Config,
    private val engineTemplate: Template
) : ConfigTemplate(cacheConfig)

open class EngineTemplateLoader<T : EngineConfigTemplate, C : NamedConfig, E : Template> {
    protected lateinit var templates: Map<Int, T>

    operator fun get(index: Int): T = templates[index] ?: throw TemplateNotFoundException(index)

    internal fun load(cacheConfigs: Map<Int, C>, engineTemplates: List<E>, constr: (C, E) -> T) {
        templates = mutableMapOf<Int, T>().apply {
            engineTemplates.forEach { engineTemplate ->
                engineTemplate.ids.forEach inner@{ id ->
                    val cacheConfig = cacheConfigs[id] ?: kotlin.run {
                        logger.warn { "Could not find config for id $id" }
                        return@inner
                    }
                    put(id, constr.invoke(cacheConfig, engineTemplate))
                }
            }
        }
        logger.info { "Loaded ${templates.size} templates" }
    }
}