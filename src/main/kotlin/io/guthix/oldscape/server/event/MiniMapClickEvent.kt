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

import io.guthix.oldscape.server.dimensions.TileUnit
import io.guthix.oldscape.server.net.state.game.ClientEvent
import io.guthix.oldscape.server.event.script.InGameEvent
import io.guthix.oldscape.server.world.World

data class MiniMapClickEvent(
    val x: TileUnit,
    val y: TileUnit,
    val type: Int,
    val mouseDx: Int,
    val mouseDy: Int,
    val angle: Int,
    val playerX: TileUnit,
    val playerY: TileUnit
) : ClientEvent, InGameEvent {
    override fun toGameEvent(world: World) = this
}