/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.world.entity

import io.guthix.oldscape.server.api.LocationBlueprints
import io.guthix.oldscape.server.dimensions.TileUnit
import io.guthix.oldscape.server.world.map.Tile

class Loc(
    id: Int,
    val type: Int,
    override val pos: Tile,
    override var orientation: Int
) : Entity() {
    private val blueprint =  LocationBlueprints[id]

    val id get() = blueprint.id

    val impenetrable get() = blueprint.impenetrable

    val clipType get() = blueprint.clipType

    val width get() = blueprint.width

    val length get() = blueprint.length

    override val sizeX get() = if (orientation == 0 || orientation == 2) width else length

    override val sizeY get() = if (orientation == 0 || orientation == 2) length else width

    val accessBlockFlags get() = if (orientation != 0) {
        (blueprint.accessBlockFlags shl orientation and 0xF) + (blueprint.accessBlockFlags shr 4 - orientation)
    } else {
        blueprint.accessBlockFlags
    }

    val slot get() = MAP_SLOTS[type]

    internal val mapKey get() = (pos.x.relativeZone.value shl 5) or (pos.y.relativeZone.value shl 2) or slot

    companion object {
        const val UNIQUE_SLOTS = 4

        val MAP_SLOTS = intArrayOf(0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3)

        fun generateMapKey(localX: TileUnit, localY: TileUnit, slot: Int) = (localX.value shl 5) or
            (localY.value shl 2) or slot
    }
}