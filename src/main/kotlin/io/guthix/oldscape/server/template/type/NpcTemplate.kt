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
package io.guthix.oldscape.server.template.type

import io.guthix.oldscape.cache.config.NpcConfig
import io.guthix.oldscape.server.PropertyHolder
import io.guthix.oldscape.server.template.EngineTemplate
import io.guthix.oldscape.server.template.EngineConfigTemplate
import io.guthix.oldscape.server.world.map.dim.TileUnit
import io.guthix.oldscape.server.world.map.dim.tiles
import mu.KotlinLogging
import kotlin.reflect.KProperty

private val logger = KotlinLogging.logger { }

data class NpcTemplate(
    private val config: NpcConfig,
    private val engineTemplate: NpcEngineTemplate
) : PropertyHolder, EngineConfigTemplate(config, engineTemplate) {
    val id: Int get() = config.id
    val size: Int get() = config.size.toInt()
    val contextMenu: Array<String?> get() = config.options
    val wanderRadius: TileUnit get() = engineTemplate.wanderRadius?.tiles ?: 0.tiles
    override val properties: MutableMap<KProperty<*>, Any?> = mutableMapOf()
}

data class NpcEngineTemplate(
    override val ids: List<Int>,
    val examine: String,
    val wanderRadius: Int?
) : EngineTemplate(ids)