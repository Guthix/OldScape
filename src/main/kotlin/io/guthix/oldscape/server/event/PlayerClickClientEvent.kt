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

import io.guthix.oldscape.server.net.state.game.ClientEvent
import io.guthix.oldscape.server.event.script.InGameEvent
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.character.player.Player

internal data class PlayerClickClientEvent(
    val playerIndex: Int,
    val buttonPressed: Boolean,
    val option: Int
) : ClientEvent {
    override fun toGameEvent(world: World): InGameEvent {
        val player = world.players[playerIndex] ?: error("Player doesn't exist.")
        return PlayerClickEvent(player, buttonPressed, player.contextMenu[option -1])
    }
}

data class PlayerClickEvent(
    val player: Player,
    val buttonPressed: Boolean,
    val option: String
) : InGameEvent