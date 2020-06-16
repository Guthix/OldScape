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
package io.guthix.oldscape.server.world.entity

class HitMark(val color: Color, val damage: Int, val delay: Int) {
    enum class Color(val id: Int) {
        GREEN(2),
        DARK_YELLOW(3),
        DARK_YELLOW_SPLAT(4),
        DARK_GREEN(5),
        MAGENTA(6),
        BLUE(12),
        BLUE_TINTED(13),
        RED(16),
        RED_TINTED(17),
        LIGHT_GREEN(18),
        ORANGE(20),
        YELLOW(22),
        YELLOW_TINTED(23),
        GREY(24),
        BLACK(25),
    }
}

