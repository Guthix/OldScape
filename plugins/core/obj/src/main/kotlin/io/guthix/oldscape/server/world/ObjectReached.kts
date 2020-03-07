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
package io.guthix.oldscape.server.world

import io.guthix.oldscape.server.event.ObjectReachedEvent
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile

on(ObjectReachedEvent::class).then {
    val tile = Tile(player.position.floor, event.x, event.y)
    val obj = world.map.removeObject(tile, event.id) ?: error(
        "Can not pick up object for id ${event.id} at position $tile."
    )
    player.inventory.addObject(obj)
}