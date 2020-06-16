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
package io.guthix.oldscape.server.pathing

enum class Direction(val mask: Int, val stepX: Int, val stepY: Int) {
    NORTH(0x1, 0, 1),
    EAST(0x2, 1, 0),
    SOUTH(0x4, 0, -1),
    WEST(0x8, -1, 0),
    NORTH_EAST(NORTH.mask or EAST.mask, EAST.stepX, NORTH.stepY),
    SOUTH_EAST(SOUTH.mask or EAST.mask, EAST.stepX, SOUTH.stepY),
    NORTH_WEST(NORTH.mask or WEST.mask, WEST.stepX, NORTH.stepY),
    SOUTH_WEST(SOUTH.mask or WEST.mask, WEST.stepX, SOUTH.stepY)
}