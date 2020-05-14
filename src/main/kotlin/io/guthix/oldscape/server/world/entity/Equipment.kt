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

import io.guthix.oldscape.server.blueprints.equipment.*
import io.guthix.oldscape.server.blueprints.equipment.NeckBlueprint

abstract class Equipment(
    override val blueprint: EquipmentBlueprint,
    quantity: Int
) : Obj(blueprint, quantity)

class HeadEquipment(
    override val blueprint: HeadBlueprint,
    quantity: Int
) : Equipment(blueprint, quantity)

class CapeEquipment(
    override val blueprint: CapeBlueprint,
    quantity: Int
) : Equipment(blueprint, quantity)

class NeckEquipment(
    override val blueprint: NeckBlueprint,
    quantity: Int
) : Equipment(blueprint, quantity)

class AmmunitionEquipment(
    override val blueprint: AmmunitionBlueprint,
    quantity: Int
) : Equipment(blueprint, quantity)

class WeaponEquipment(
    override val blueprint: WeaponBlueprint,
    quantity: Int
) : Equipment(blueprint, quantity)

class ShieldEquipment(
    override val blueprint: ShieldBlueprint,
    quantity: Int
) : Equipment(blueprint, quantity)

class TwoHandEquipment(
    override val blueprint: TwoHandedBlueprint,
    quantity: Int
) : Equipment(blueprint, quantity)

class BodyEquipment(
    override val blueprint: BodyBlueprint,
    quantity: Int
) : Equipment(blueprint, quantity)

class LegsEquipment(
    override val blueprint: LegsBlueprint,
    quantity: Int
) : Equipment(blueprint, quantity)

class HandsEquipment(
    override val blueprint: HandsBlueprint,
    quantity: Int
) : Equipment(blueprint, quantity)

class FeetEquipment(
    override val blueprint: FeetBlueprint,
    quantity: Int
) : Equipment(blueprint, quantity)

class RingEquipment(
    override val blueprint: TwoHandedBlueprint,
    quantity: Int
) : Equipment(blueprint, quantity)