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

import io.guthix.oldscape.server.dimensions.TileUnit
import io.guthix.oldscape.server.dimensions.floors
import io.guthix.oldscape.server.dimensions.tiles
import io.guthix.oldscape.server.world.map.Tile
import java.util.*

abstract class CharacterVisual {
    abstract val updateFlags: SortedSet<out UpdateType>

    var movementType = MovementUpdateType.STAY

    var position: Tile = Tile(0.floors, 3231.tiles, 3222.tiles)

    var lastPostion = position.copy(x = position.x - 1.tiles)

    var followPosition = lastPostion.copy()

    open var orientation: Int = 0

    fun getOrientation(prev: Tile, new: Tile) = getOrientation(new.x - prev.x, new.y - prev.y)

    fun getOrientation(dx: TileUnit, dy: TileUnit) = moveDirection[2 - dy.value][dx.value + 2]

    abstract class UpdateType(private val priority: Int, internal val mask: Int) : Comparable<UpdateType> {
        override fun compareTo(other: UpdateType) = when {
            priority < other.priority -> -1
            priority > other.priority -> 1
            else -> 0
        }
    }

    enum class MovementUpdateType { TELEPORT, RUN, WALK, STAY }

    companion object {
        private val moveDirection = arrayOf(
            intArrayOf(768, 768, 1024, 1280, 1280),
            intArrayOf(768, 768, 1024, 1280, 1280),
            intArrayOf(512, 512, -1, 1536, 1536),
            intArrayOf(256, 256, 0, 1792, 1792),
            intArrayOf(256, 256, 0, 1792, 1792)
        )
    }
}