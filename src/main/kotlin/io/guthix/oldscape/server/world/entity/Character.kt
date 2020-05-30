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

abstract class Character(
    val index: Int,
    internal open val visualInterestManager: CharacterVisual
) : Entity() {
    abstract val size: TileUnit

    override val sizeX get() = size

    override val sizeY get() = size

    override val pos: Tile get() = visualInterestManager.pos

    val lastPos: Tile get() = visualInterestManager.lastPos
}