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
package io.guthix.oldscape.server.world.entity.character.player

data class Appearance(
    var gender: Gender,
    var isSkulled: Boolean,
    var prayerIcon: Int,
    val style: Style,
    val equipment: Equipment,
    var colours: Colours,
    var animations: Animations
) {
    data class Equipment(
        var head: Head?,
        var cape: Cape?,
        var neck: Neck?,
        var weapon: Weapon?,
        var body: Body?,
        var shield: Shield?,
        var legs: Legs?,
        var hands: Hands?,
        var feet: Feet?
    )

    data class Style(
        val hair: Int,
        val beard: Int,
        val torso: Int,
        val arms: Int,
        val legs: Int,
        val hands: Int,
        val feet: Int
    )

    data class Colours(
        var hair: Int,
        var torso: Int,
        var legs: Int,
        var feet: Int,
        var skin: Int
    )

    data class Animations(
        var stand: Int,
        var turn: Int,
        var walk: Int,
        var turn180: Int,
        var turn90CW: Int,
        var turn90CCW: Int,
        var run: Int
    )

    enum class Gender(val opcode: Int) { MALE(0), FEMALE(1) }
}