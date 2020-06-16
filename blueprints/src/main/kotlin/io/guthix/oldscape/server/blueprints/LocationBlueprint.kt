/**
 * This file is part of Guthix OldScape-Server.
 *
 * Guthix OldScape-Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
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