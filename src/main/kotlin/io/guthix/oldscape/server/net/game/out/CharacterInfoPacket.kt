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
package io.guthix.oldscape.server.net.game.out

import io.guthix.oldscape.server.dimensions.tiles
import io.guthix.oldscape.server.world.map.Tile

abstract class CharacterInfoPacket {
    companion object {
        val INTEREST_SIZE = 32.tiles

        private val INTEREST_RANGE = INTEREST_SIZE / 2.tiles

        fun Tile.isInterestedIn(other: Tile) = withInDistanceOf(other, INTEREST_RANGE)
    }
}