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
package io.guthix.oldscape.server.inventory

import io.guthix.oldscape.server.event.InventoryObjectClickEvent
import io.guthix.oldscape.server.event.script.EventBus
import io.guthix.oldscape.server.blueprints.equipment.*
import io.guthix.oldscape.server.event.*


on(InventoryObjectClickEvent::class).then {
    if (event.objBlueprint is EquipmentBlueprint) {
        EventBus.schedule(
            InventoryEquipmentClickEvent(event.interfaceId, event.interfaceSlot, event.inventorySlot, event.option,
                event.objBlueprint as EquipmentBlueprint
            ), world, player
        )
    }
}

on(InventoryEquipmentClickEvent::class).then {
    when (event.objBlueprint) {
        is WeaponBlueprint -> EventBus.schedule(
            InventoryWeaponClickEvent(event.interfaceId, event.interfaceSlot, event.inventorySlot, event.option,
                event.objBlueprint as WeaponBlueprint
            ), world, player
        )
        is ShieldBlueprint -> EventBus.schedule(
            InventoryShieldClickEvent(event.interfaceId, event.interfaceSlot, event.inventorySlot, event.option,
                event.objBlueprint as ShieldBlueprint
            ), world, player
        )
        is TwoHandedBlueprint -> EventBus.schedule(
            InventoryTwoHandedClickEvent(event.interfaceId, event.interfaceSlot, event.inventorySlot, event.option,
                event.objBlueprint as TwoHandedBlueprint
            ), world, player
        )
        is AmmunitionBlueprint -> EventBus.schedule(
            InventoryAmmunitionClickEvent(event.interfaceId, event.interfaceSlot, event.inventorySlot, event.option,
                event.objBlueprint as AmmunitionBlueprint
            ), world, player
        )
        is HeadBlueprint -> EventBus.schedule(
            InventoryHeadClickEvent(event.interfaceId, event.interfaceSlot, event.inventorySlot, event.option,
                event.objBlueprint as HeadBlueprint
            ), world, player
        )
        is BodyBlueprint -> EventBus.schedule(
            InventoryBodyClickEvent(event.interfaceId, event.interfaceSlot, event.inventorySlot, event.option,
                event.objBlueprint as BodyBlueprint
            ), world, player
        )
        is LegsBlueprint -> EventBus.schedule(
            InventoryLegsClickEvent(event.interfaceId, event.interfaceSlot, event.inventorySlot, event.option,
                event.objBlueprint as LegsBlueprint
            ), world, player
        )
        is FeetBlueprint -> EventBus.schedule(
            InventoryFeetClickEvent(event.interfaceId, event.interfaceSlot, event.inventorySlot, event.option,
                event.objBlueprint as FeetBlueprint
            ), world, player
        )
        is HandsBlueprint -> EventBus.schedule(
            InventoryHandsClickEvent(event.interfaceId, event.interfaceSlot, event.inventorySlot, event.option,
                event.objBlueprint as HandsBlueprint
            ), world, player
        )
        is NeckBlueprint -> EventBus.schedule(
            InventoryNeckClickEvent(event.interfaceId, event.interfaceSlot, event.inventorySlot, event.option,
                event.objBlueprint as NeckBlueprint
            ), world, player
        )
        is RingBlueprint -> EventBus.schedule(
            InventoryRingClickEvent(event.interfaceId, event.interfaceSlot, event.inventorySlot, event.option,
                event.objBlueprint as RingBlueprint
            ), world, player
        )
        is CapeBlueprint -> EventBus.schedule(
            InventoryCapeClickEvent(event.interfaceId, event.interfaceSlot, event.inventorySlot, event.option,
                event.objBlueprint as CapeBlueprint
            ), world, player
        )
    }
}