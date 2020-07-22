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
package io.guthix.oldscape.server.blueprints

import io.guthix.oldscape.cache.config.LocationConfig
import io.guthix.oldscape.server.dimensions.TileUnit
import io.guthix.oldscape.server.dimensions.tiles

class LocationBlueprint private constructor(
    val id: Int,
    val name: String,
    val width: TileUnit,
    val length: TileUnit,
    val mapIconId: Int?,
    val clipType: Int,
    val isClipped: Boolean,
    val isHollow: Boolean,
    val impenetrable: Boolean,
    val accessBlockFlags: Int,
    val animationId: Int?,
    val options: Array<String?>
) {
    companion object {
        fun create(config: LocationConfig): LocationBlueprint {
            return LocationBlueprint(
                config.id,
                config.name,
                config.width.toInt().tiles,
                config.length.toInt().tiles,
                config.mapIconId,
                config.clipType,
                config.isClipped,
                config.isHollow,
                config.impenetrable,
                config.accessBlock.toInt(),
                config.animationId,
                config.options
            )
        }
    }
}