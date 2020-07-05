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
package io.guthix.oldscape.server.equipment

import io.guthix.oldscape.server.event.*
import io.guthix.oldscape.server.world.entity.*

on(InvObjClickEvent::class).then {
    val obj = player.topInterface.inventory.removeObject(inventorySlot) ?: return@then
    when (obj) {
        is TwoHandEquipment -> {
            player.topInterface.equipment.setObject(obj.slot.id, obj)
            player.equip(obj)
            player.equip(shield = null)
            EventBus.schedule(WeaponEquipedEvent(obj, player, world))
            EventBus.schedule(TwoHandEquipedEvent(obj, player, world))
        }
        is WeaponEquipment -> {
            player.topInterface.equipment.setObject(obj.slot.id, obj)
            player.equip(obj)
            EventBus.schedule(WeaponEquipedEvent(obj, player, world))
        }
        is ShieldEquipment -> {
            player.topInterface.equipment.setObject(obj.slot.id, obj)
            player.equip(obj)
            EventBus.schedule(ShieldEquipedEvent(obj, player, world))
        }
        is HeadEquipment -> {
            player.topInterface.equipment.setObject(obj.slot.id, obj)
            player.equip(obj)
            EventBus.schedule(HeadEquipedEvent(obj, player, world))
        }
        is BodyEquipment -> {
            player.topInterface.equipment.setObject(obj.slot.id, obj)
            player.equip(obj)
            EventBus.schedule(BodyEquipedEvent(obj, player, world))
        }
        is LegEquipment -> {
            player.topInterface.equipment.setObject(obj.slot.id, obj)
            player.equip(obj)
            EventBus.schedule(LegEquipedEvent(obj, player, world))
        }
        is AmmunitionEquipment -> {
            player.topInterface.equipment.setObject(obj.slot.id, obj)
            player.equip(obj)
            EventBus.schedule(AmmunitionEquipedEvent(obj, player, world))
        }
        is CapeEquipment -> {
            player.topInterface.equipment.setObject(obj.slot.id, obj)
            player.equip(obj)
            EventBus.schedule(CapeEquipedEvent(obj, player, world))
        }
        is RingEquipment -> {
            player.topInterface.equipment.setObject(obj.slot.id, obj)
            player.equip(obj)
            EventBus.schedule(RingEquipedEvent(obj, player, world))
        }
        is NeckEquipment -> {
            player.topInterface.equipment.setObject(obj.slot.id, obj)
            player.equip(obj)
            EventBus.schedule(NeckEquipedEvent(obj, player, world))
        }
        is HandEquipment -> {
            player.topInterface.equipment.setObject(obj.slot.id, obj)
            player.equip(obj)
            EventBus.schedule(HandEquipedEvent(obj, player, world))
        }
        is FeetEquipment -> {
            player.topInterface.equipment.setObject(obj.slot.id, obj)
            player.equip(obj)
            EventBus.schedule(FeetEquipedEvent(obj, player, world))
        }
        else -> throw IllegalStateException("Object $obj should be equipment.")
    }
    EventBus.schedule(ObjEquipedEvent(obj as Equipment, player, world))
}