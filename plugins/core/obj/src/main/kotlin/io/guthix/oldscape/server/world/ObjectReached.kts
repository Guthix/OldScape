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
package io.guthix.oldscape.server.world

import io.guthix.oldscape.server.event.ObjectReachedEvent
import io.guthix.oldscape.server.world.map.Tile

on(ObjectReachedEvent::class).then {
    val tile = Tile(player.pos.floor, x, y)
    val obj = world.map.removeObject(tile, id) ?: error(
        "Can not pick up object for id ${id} at position $tile."
    )
    player.topInterface.inventory.setObject(obj)
}