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
    if(event.objBlueprint is EquipmentBlueprint) {
        EventBus.schedule(event as InventoryEquipmentClickEvent, world, player)
    }
}

on(InventoryEquipmentClickEvent::class).then {
    when(event.objBlueprint) {
        is WeaponBlueprint -> EventBus.schedule(event as InventoryWeaponClickEvent, world, player)
        is ShieldBlueprint -> EventBus.schedule(event as InventoryShieldClickEvent, world, player)
        is TwoHandedBlueprint -> EventBus.schedule(event as InventoryTwoHandedClickEvent, world, player)
        is AmmunitionBlueprint -> EventBus.schedule(event as InventoryAmmunitionClickEvent, world, player)
        is HeadBlueprint -> EventBus.schedule(event as InventoryHeadEquipmentClickEvent, world, player)
        is BodyBlueprint -> EventBus.schedule(event as InventoryBodyClickEvent, world, player)
        is LegsBlueprint -> EventBus.schedule(event as InventoryLegsClickEvent, world, player)
        is FeetBlueprint -> EventBus.schedule(event as InventoryFeetClickEvent, world, player)
        is HandsBlueprint -> EventBus.schedule(event as InventoryHandsClickEvent, world, player)
        is NeckBlueprint -> EventBus.schedule(event as InventoryNeckEquipmentClickEvent, world, player)
        is RingBlueprint -> EventBus.schedule(event as InventoryRingClickEvent, world, player)
        is CapeBlueprint -> EventBus.schedule(event as InventoryCapeClickEvent, world, player)
    }
}