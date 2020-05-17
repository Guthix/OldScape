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

import io.guthix.oldscape.server.api.ObjectBlueprints
import io.guthix.oldscape.server.blueprints.ObjectBlueprint
import io.guthix.oldscape.server.blueprints.equipment.*

open class Obj(id: Int, var quantity: Int) {
    protected open val blueprint: ObjectBlueprint = ObjectBlueprints[id]
    
    val id get() = blueprint.id
    val name get() = blueprint.name
    val weight get() = blueprint.weight
    val examines get() = blueprint.examines
    val isStackable get() = blueprint.isStackable
    val isTradable get() = blueprint.isTradable
    val notedId get() = blueprint.notedId
    val isNoted get() = blueprint.isNoted
    val placeHolderId get() = blueprint.placeHolderId
    val isPlaceHolder get() = blueprint.isPlaceHolder
    val interfaceOperations get() = blueprint.interfaceOperations
    val groundOperations get() = blueprint.groundOperations
}

abstract class Equipment(id: Int, quantity: Int) : Obj(id, quantity) {
    override val blueprint: EquipmentBlueprint = ObjectBlueprints[id]

    val slot get() = blueprint.slot
}

class HeadEquipment(id: Int, quantity: Int) : Equipment(id, quantity) {
    override val blueprint: HeadBlueprint = ObjectBlueprints[id]

    val coversFace get() = blueprint.coversFace
    val coversHair get() = blueprint.coversHair
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

    val isFullBody get() = blueprint.isFullBody
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