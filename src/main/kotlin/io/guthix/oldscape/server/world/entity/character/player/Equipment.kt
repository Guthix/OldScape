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

import io.guthix.oldscape.server.api.blueprint.ObjectBlueprint
import io.guthix.oldscape.server.world.entity.Obj

abstract class Equipment(
    val slot: Int,
    val attackBonus: StyleBonus,
    val defenceBonus: StyleBonus,
    val strengthBonus: StrengthBonus,
    val prayerBonus: Int,
    amount: Int,
    blueprint: ObjectBlueprint
) : Obj(blueprint, amount) {
    class StyleBonus(
        val stab: Int,
        val slash: Int,
        val crush: Int,
        val magic: Int,
        val range: Int
    )

    class StrengthBonus(
        val melee: Int,
        val range: Int,
        val magic: Int
    )
}

class Head(
    val coversScalp: Boolean,
    val coversFace: Boolean,
    attackBonus: StyleBonus,
    defenceBonus: StyleBonus,
    strengthBonus: StrengthBonus,
    prayerBonus: Int,
    blueprint: ObjectBlueprint
) : Equipment(0, attackBonus, defenceBonus, strengthBonus, prayerBonus, 1, blueprint)

class Cape(
    attackBonus: StyleBonus,
    defenceBonus: StyleBonus,
    strengthBonus: StrengthBonus,
    prayerBonus: Int,
    blueprint: ObjectBlueprint
) : Equipment(1, attackBonus, defenceBonus, strengthBonus, prayerBonus, 1, blueprint)

class Neck(
    attackBonus: StyleBonus,
    defenceBonus: StyleBonus,
    strengthBonus: StrengthBonus,
    prayerBonus: Int,
    blueprint: ObjectBlueprint
) : Equipment(2, attackBonus, defenceBonus, strengthBonus, prayerBonus, 1, blueprint)

class Ammunition(
    attackBonus: StyleBonus,
    defenceBonus: StyleBonus,
    strengthBonus: StrengthBonus,
    prayerBonus: Int,
    amount: Int,
    blueprint: ObjectBlueprint
) : Equipment(3, attackBonus, defenceBonus, strengthBonus, prayerBonus, amount, blueprint)

class Weapon(
    attackBonus: StyleBonus,
    defenceBonus: StyleBonus,
    strengthBonus: StrengthBonus,
    prayerBonus: Int,
    blueprint: ObjectBlueprint
) : Equipment(4, attackBonus, defenceBonus, strengthBonus, prayerBonus, 1, blueprint)

class Body(
    val fullBody: Boolean,
    attackBonus: StyleBonus,
    defenceBonus: StyleBonus,
    strengthBonus: StrengthBonus,
    prayerBonus: Int,
    blueprint: ObjectBlueprint
) : Equipment(5, attackBonus, defenceBonus, strengthBonus, prayerBonus, 1, blueprint)

class Shield(
    attackBonus: StyleBonus,
    defenceBonus: StyleBonus,
    strengthBonus: StrengthBonus,
    prayerBonus: Int,
    blueprint: ObjectBlueprint
) : Equipment(6, attackBonus, defenceBonus, strengthBonus, prayerBonus, 1, blueprint)

class Legs(
    attackBonus: StyleBonus,
    defenceBonus: StyleBonus,
    strengthBonus: StrengthBonus,
    prayerBonus: Int,
    blueprint: ObjectBlueprint
) : Equipment(7, attackBonus, defenceBonus, strengthBonus, prayerBonus, 1, blueprint)

class Ring(
    attackBonus: StyleBonus,
    defenceBonus: StyleBonus,
    strengthBonus: StrengthBonus,
    prayerBonus: Int,
    blueprint: ObjectBlueprint
) : Equipment(8, attackBonus, defenceBonus, strengthBonus, prayerBonus, 1, blueprint)

class Hands(
    attackBonus: StyleBonus,
    defenceBonus: StyleBonus,
    strengthBonus: StrengthBonus,
    prayerBonus: Int,
    blueprint: ObjectBlueprint
) : Equipment(9, attackBonus, defenceBonus, strengthBonus, prayerBonus, 1, blueprint)

class Feet(
    attackBonus: StyleBonus,
    defenceBonus: StyleBonus,
    strengthBonus: StrengthBonus,
    prayerBonus: Int,
    blueprint: ObjectBlueprint
) : Equipment(10, attackBonus, defenceBonus, strengthBonus, prayerBonus, 1, blueprint)