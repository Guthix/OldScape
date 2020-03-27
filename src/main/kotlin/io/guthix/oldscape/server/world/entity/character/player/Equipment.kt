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

import io.guthix.oldscape.server.blueprints.equipment.*
import io.guthix.oldscape.server.world.entity.Obj

class HeadEquipment(
    override val blueprint: HeadEquipmentBlueprint,
    quantity: Int
) : Obj(blueprint, quantity)

class CapeEquipment(
    override val blueprint: CapeBlueprint,
    quantity: Int
) : Obj(blueprint, quantity)

class NeckEquipment(
    override val blueprint: NeckEquipmentBlueprint,
    quantity: Int
) : Obj(blueprint, quantity)

class AmmunitionEquipment(
    override val blueprint: AmmunitionBlueprint,
    quantity: Int
) : Obj(blueprint, quantity)

class WeaponEquipment(
    override val blueprint: WeaponBlueprint,
    quantity: Int
) : Obj(blueprint, quantity)

class ShieldEquipment(
    override val blueprint: ShieldBlueprint,
    quantity: Int
) : Obj(blueprint, quantity)

class BodyEquipment(
    override val blueprint: BodyBlueprint,
    quantity: Int
) : Obj(blueprint, quantity)

class LegsEquipment(
    override val blueprint: LegsBlueprint,
    quantity: Int
) : Obj(blueprint, quantity)

class HandsEquipment(
    override val blueprint: HandsBlueprint,
    quantity: Int
) : Obj(blueprint, quantity)

class FeetEquipment(
    override val blueprint: FeetBlueprint,
    quantity: Int
) : Obj(blueprint, quantity)

class RingEquipment(
    override val blueprint: TwoHandedBlueprint,
    quantity: Int
) : Obj(blueprint, quantity)