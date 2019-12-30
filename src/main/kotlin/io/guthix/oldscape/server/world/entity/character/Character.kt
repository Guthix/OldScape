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
package io.guthix.oldscape.server.world.entity.character

import io.guthix.oldscape.server.world.entity.Entity
import io.guthix.oldscape.server.world.mapsquare.floor
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import io.guthix.oldscape.server.world.mapsquare.zone.tile.tile
import kotlin.reflect.KProperty

abstract class Character(attributes: MutableMap<KProperty<*>, Any?>) : Entity(attributes) {
    val position = Tile(0.floor, 3222.tile, 3218.tile)

    val lastPostion = position

    val interestMovementUpdateType = MovementUpdateType.STAY

    abstract val updateFlags: MutableList<out UpdateType>

    abstract class UpdateType(internal val mask: Int)

    enum class MovementUpdateType { TELEPORT, RUN, WALK, STAY }
}