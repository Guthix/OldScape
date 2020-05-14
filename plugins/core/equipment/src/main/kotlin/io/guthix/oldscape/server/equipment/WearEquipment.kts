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

import io.guthix.oldscape.server.event.*
import io.guthix.oldscape.server.world.entity.HeadEquipment

on(InventoryHeadClickEvent::class).where { event.option == "Wear" }.then {
    val obj = player.topInterface.inventory.removeObject(event.inventorySlot) ?: return@then
    player.topInterface.equipment.setObject(event.objBlueprint.slot.id, obj)
    player.equip(HeadEquipment(event.objBlueprint, 1)) // TODO get typed equipment from obj
}