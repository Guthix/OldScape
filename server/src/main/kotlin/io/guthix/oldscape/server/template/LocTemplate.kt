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

import io.guthix.oldscape.cache.config.LocationConfig
import io.guthix.oldscape.server.world.map.dim.TileUnit
import io.guthix.oldscape.server.world.map.dim.tiles
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

data class LocTemplate(private val config: LocationConfig) : BaseTemplate(config) {
    val id: Int get() = config.id
    val name: String get() = config.name
    val width: TileUnit get() = config.width.toInt().tiles
    val length: TileUnit get() = config.length.toInt().tiles
    val mapIconId: Int? get() = config.mapIconId
    val clipType: Int get() = config.clipType
    val isClipped: Boolean get() = config.isClipped
    val isHollow: Boolean get() = config.isHollow
    val impenetrable: Boolean get() = config.impenetrable
    val accessBlockFlags: Int get() = config.accessBlock.toInt()
    val animationId: Int? get() = config.animationId
    val options: Array<String?> get() = config.options
}