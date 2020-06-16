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
package io.guthix.oldscape.server.equipment

import io.guthix.oldscape.server.event.InventoryObjectClickEvent
import io.guthix.oldscape.server.world.entity.*

on(InventoryObjectClickEvent::class).where { event.option == "Wear" }.then {
    val obj = player.topInterface.inventory.removeObject(event.inventorySlot) ?: return@then
    when (obj) {
        is WeaponEquipment -> {
            player.topInterface.equipment.setObject(obj.slot.id, obj)
            player.equip(obj)
        }
        is ShieldEquipment -> {
            player.topInterface.equipment.setObject(obj.slot.id, obj)
            player.equip(obj)
        }
        is HeadEquipment -> {
            player.topInterface.equipment.setObject(obj.slot.id, obj)
            player.equip(obj)
        }
        is BodyEquipment -> {
            player.topInterface.equipment.setObject(obj.slot.id, obj)
            player.equip(obj)
        }
        is LegsEquipment -> {
            player.topInterface.equipment.setObject(obj.slot.id, obj)
            player.equip(obj)
        }
        is AmmunitionEquipment -> {
            player.topInterface.equipment.setObject(obj.slot.id, obj)
            player.equip(obj)
        }
        is CapeEquipment -> {
            player.topInterface.equipment.setObject(obj.slot.id, obj)
            player.equip(obj)
        }
        is RingEquipment -> {
            player.topInterface.equipment.setObject(obj.slot.id, obj)
            player.equip(obj)
        }
        is NeckEquipment -> {
            player.topInterface.equipment.setObject(obj.slot.id, obj)
            player.equip(obj)
        }
        is HandEquipment -> {
            player.topInterface.equipment.setObject(obj.slot.id, obj)
            player.equip(obj)
        }
        is FeetEquipment -> {
            player.topInterface.equipment.setObject(obj.slot.id, obj)
            player.equip(obj)
        }
    }
}