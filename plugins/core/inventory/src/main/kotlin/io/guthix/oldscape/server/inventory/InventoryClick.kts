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
            ), player, world
        )
    }
}

on(InventoryEquipmentClickEvent::class).then {
    when (event.objBlueprint) {
        is WeaponBlueprint -> EventBus.schedule(
            InventoryWeaponClickEvent(event.interfaceId, event.interfaceSlot, event.inventorySlot, event.option,
                event.objBlueprint as WeaponBlueprint
            ), player, world
        )
        is ShieldBlueprint -> EventBus.schedule(
            InventoryShieldClickEvent(event.interfaceId, event.interfaceSlot, event.inventorySlot, event.option,
                event.objBlueprint as ShieldBlueprint
            ), player, world
        )
        is TwoHandedBlueprint -> EventBus.schedule(
            InventoryTwoHandedClickEvent(event.interfaceId, event.interfaceSlot, event.inventorySlot, event.option,
                event.objBlueprint as TwoHandedBlueprint
            ), player, world
        )
        is AmmunitionBlueprint -> EventBus.schedule(
            InventoryAmmunitionClickEvent(event.interfaceId, event.interfaceSlot, event.inventorySlot, event.option,
                event.objBlueprint as AmmunitionBlueprint
            ), player, world
        )
        is HeadBlueprint -> EventBus.schedule(
            InventoryHeadClickEvent(event.interfaceId, event.interfaceSlot, event.inventorySlot, event.option,
                event.objBlueprint as HeadBlueprint
            ), player, world
        )
        is BodyBlueprint -> EventBus.schedule(
            InventoryBodyClickEvent(event.interfaceId, event.interfaceSlot, event.inventorySlot, event.option,
                event.objBlueprint as BodyBlueprint
            ), player, world
        )
        is LegsBlueprint -> EventBus.schedule(
            InventoryLegsClickEvent(event.interfaceId, event.interfaceSlot, event.inventorySlot, event.option,
                event.objBlueprint as LegsBlueprint
            ), player, world
        )
        is FeetBlueprint -> EventBus.schedule(
            InventoryFeetClickEvent(event.interfaceId, event.interfaceSlot, event.inventorySlot, event.option,
                event.objBlueprint as FeetBlueprint
            ), player, world
        )
        is HandsBlueprint -> EventBus.schedule(
            InventoryHandsClickEvent(event.interfaceId, event.interfaceSlot, event.inventorySlot, event.option,
                event.objBlueprint as HandsBlueprint
            ), player, world
        )
        is NeckBlueprint -> EventBus.schedule(
            InventoryNeckClickEvent(event.interfaceId, event.interfaceSlot, event.inventorySlot, event.option,
                event.objBlueprint as NeckBlueprint
            ), player, world
        )
        is RingBlueprint -> EventBus.schedule(
            InventoryRingClickEvent(event.interfaceId, event.interfaceSlot, event.inventorySlot, event.option,
                event.objBlueprint as RingBlueprint
            ), player, world
        )
        is CapeBlueprint -> EventBus.schedule(
            InventoryCapeClickEvent(event.interfaceId, event.interfaceSlot, event.inventorySlot, event.option,
                event.objBlueprint as CapeBlueprint
            ), player, world
        )
    }
}