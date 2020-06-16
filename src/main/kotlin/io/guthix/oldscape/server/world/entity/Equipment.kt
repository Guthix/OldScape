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

import io.guthix.oldscape.server.api.ObjectBlueprints
import io.guthix.oldscape.server.blueprints.CombatBonus
import io.guthix.oldscape.server.blueprints.StyleBonus
import io.guthix.oldscape.server.blueprints.equipment.*

abstract class Equipment(id: Int, quantity: Int) : Obj(id, quantity) {
    override val blueprint: EquipmentBlueprint = ObjectBlueprints[id]

    val slot: EquipmentBlueprint.EquipmentSlot get() = blueprint.slot

    val attackBonus: StyleBonus get() = blueprint.attackBonus

    val defenceBonus: StyleBonus get() = blueprint.defenceBonus

    val strengthBonus: CombatBonus get() = blueprint.strengthBonus

    val prayerBonus: Int get() = blueprint.prayerBonus
}

class HeadEquipment(id: Int, quantity: Int) : Equipment(id, quantity) {
    override val blueprint: HeadBlueprint = ObjectBlueprints[id]

    val coversFace: Boolean get() = blueprint.coversFace
    val coversHair: Boolean get() = blueprint.coversHair
}

class CapeEquipment(id: Int, quantity: Int) : Equipment(id, quantity) {
    override val blueprint: CapeBlueprint = ObjectBlueprints[id]
}

class NeckEquipment(id: Int, quantity: Int) : Equipment(id, quantity) {
    override val blueprint: NeckBlueprint = ObjectBlueprints[id]
}

class AmmunitionEquipment(id: Int, quantity: Int) : Equipment(id, quantity) {
    override val blueprint: AmmunitionBlueprint = ObjectBlueprints[id]
}

class WeaponEquipment(id: Int, quantity: Int) : Equipment(id, quantity) {
    override val blueprint: WeaponBlueprint = ObjectBlueprints[id]
}

class ShieldEquipment(id: Int, quantity: Int) : Equipment(id, quantity) {
    override val blueprint: ShieldBlueprint = ObjectBlueprints[id]
}

class TwoHandEquipment(id: Int, quantity: Int) : Equipment(id, quantity) {
    override val blueprint: TwoHandedBlueprint = ObjectBlueprints[id]
}

class BodyEquipment(id: Int, quantity: Int) : Equipment(id, quantity) {
    override val blueprint: BodyBlueprint = ObjectBlueprints[id]

    val isFullBody: Boolean get() = blueprint.isFullBody
}

class LegsEquipment(id: Int, quantity: Int) : Equipment(id, quantity) {
    override val blueprint: LegsBlueprint = ObjectBlueprints[id]
}

class HandEquipment(id: Int, quantity: Int) : Equipment(id, quantity) {
    override val blueprint: HandsBlueprint = ObjectBlueprints[id]
}

class FeetEquipment(id: Int, quantity: Int) : Equipment(id, quantity) {
    override val blueprint: FeetBlueprint = ObjectBlueprints[id]
}

class RingEquipment(id: Int, quantity: Int) : Equipment(id, quantity) {
    override val blueprint: RingBlueprint = ObjectBlueprints[id]
}