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
package io.guthix.oldscape.server.event

import io.guthix.oldscape.server.api.blueprint.ObjectBlueprints
import io.guthix.oldscape.server.event.script.InGameEvent
import io.guthix.oldscape.server.net.state.game.ClientEvent
import io.guthix.oldscape.server.world.World

internal data class InventoryClickClientEvent(
    val interfaceId: Int,
    val interfaceSlot: Int,
    val objId: Int,
    val inventorySlot: Int,
    val option: Int
) : ClientEvent {
    override fun toGameEvent(world: World): InGameEvent {
        val op = ObjectBlueprints[objId].iop[option - 1] ?: error("Item $objId doesn't exist.")
        return InventoryClickEvent(interfaceId, interfaceSlot, objId, inventorySlot, op)
    }
}

data class InventoryClickEvent(
    val interfaceId: Int,
    val interfaceSlot: Int,
    val objId: Int,
    val inventorySlot: Int,
    val option: String
) : InGameEvent