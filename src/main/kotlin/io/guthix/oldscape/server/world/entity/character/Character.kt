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
package io.guthix.oldscape.server.world.entity.character

import io.guthix.oldscape.server.dimensions.TileUnit
import io.guthix.oldscape.server.dimensions.tiles
import io.guthix.oldscape.server.world.entity.Entity
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import java.util.*
import kotlin.reflect.KProperty

abstract class Character(
    open val index: Int,
    position: Tile,
    attributes: MutableMap<KProperty<*>, Any?>
) : Entity(position, attributes) {
    var lastPostion = position.copy(x = position.x - 1.tiles)

    var movementType = MovementUpdateType.STAY

    abstract val updateFlags: SortedSet<out UpdateType>

    abstract class UpdateType(internal val priority: Int, internal val mask: Int) : Comparable<UpdateType> {
        override fun compareTo(other: UpdateType) = when {
            priority < other.priority -> -1
            priority > other.priority -> 1
            else -> 0
        }
    }

    enum class MovementUpdateType { TELEPORT, RUN, WALK, STAY }

    fun getOrientation(lastLocation: Tile, location: Tile): Int {
        val dx = location.x - lastLocation.x
        val dy = location.y - lastLocation.y
        return getOrientation(dx, dy)
    }

    fun getOrientation(dx: TileUnit, dy: TileUnit): Int = ORIENTATION[2 - dy.value][dx.value + 2]

    companion object {
        private val ORIENTATION = arrayOf(
            intArrayOf(768, 768, 1024, 1280, 1280),
            intArrayOf(768, 768, 1024, 1280, 1280),
            intArrayOf(512, 512, -1, 1536, 1536),
            intArrayOf(256, 256, 0, 1792, 1792),
            intArrayOf(256, 256, 0, 1792, 1792)
        )
    }
}