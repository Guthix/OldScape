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
import io.guthix.oldscape.server.PropertyHolder
import io.guthix.oldscape.server.event.InitializeTemplateEvent
import io.guthix.oldscape.server.event.WorldInitializedEvent
import io.guthix.oldscape.server.plugin.Script
import io.guthix.oldscape.server.readYaml
import mu.KotlinLogging
import kotlin.reflect.KProperty

private val logger = KotlinLogging.logger { }

abstract class Template(open val ids: List<Int>)

abstract class BaseTemplate(config: Config) : PropertyHolder {
    override val properties: MutableMap<KProperty<*>, Any?> = mutableMapOf()
}

class TemplateNotFoundException(id: Int) : Exception("Template with index $id not found.")

abstract class TemplateLoader<B : BaseTemplate> {
    private lateinit var baseTemplates: Map<Int, B>

    operator fun get(index: Int): B = baseTemplates[index] ?: throw TemplateNotFoundException(index)

    internal fun <C: Config> load(configs: Map<Int, C>, factory: (C) -> B) {
        val temp = mutableMapOf<Int, B>()
        configs.forEach { (id, config) ->
            temp[id] = factory(config)
        }
        baseTemplates = temp
        logger.info { "Loaded ${baseTemplates.size} ${temp.values.first()::class.simpleName}s" }
    }
}

inline fun <reified T: Template, B : BaseTemplate> Script.loadTemplates(
    relativePath: String,
    loader: TemplateLoader<B>,
    property: KProperty<T?>
) {
    val logger = KotlinLogging.logger { }
    on(InitializeTemplateEvent::class).then {
        val templates: List<T> = readYaml(relativePath)
        templates.forEach {  template ->
            template.ids.forEach { id ->
                val baseTemplate = loader[id]
                baseTemplate.properties[property] = template
            }
        }
        logger.info { "Loaded ${templates.size} ${T::class.simpleName}s" }
    }
}