/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.world.entity

import io.guthix.oldscape.server.api.blueprint.LocationBlueprint
import io.guthix.oldscape.server.world.mapsquare.FloorUnit
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import io.guthix.oldscape.server.world.mapsquare.zone.tile.TileUnit
import kotlin.reflect.KProperty

class Loc(
    override val position: Tile,
    val blueprint: LocationBlueprint,
    val type: Int,
    val orientation: Int,
    override val attributes: MutableMap<KProperty<*>, Any?> = mutableMapOf()
) : Entity(position, attributes) {
    override val sizeX get() = if (orientation == 0 || orientation == 2) blueprint.width else blueprint.length

    override val sizeY get() = if (orientation == 0 || orientation == 2) blueprint.length else blueprint.width

    val accessBlockFlags get() = if (orientation != 0) {
        (blueprint.accessBlockFlags shl orientation and 0xF) + (blueprint.accessBlockFlags shr 4 - orientation)
    } else {
        blueprint.accessBlockFlags
    }

    val slot get() = MAP_SLOTS[type]

    internal val mapKey get() = (position.x.relativeZone.value shl 5) or (position.y.relativeZone.value shl 2) or slot

    companion object {
        const val UNIQUE_SLOTS = 4

        val MAP_SLOTS = intArrayOf(0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3)

        fun generateMapKey(localX: TileUnit, localY: TileUnit, slot: Int) =
            (localX.value shl 5) or (localY.value shl 2) or slot
    }
}