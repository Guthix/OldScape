/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.interest

import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.EntityAttribute
import io.guthix.oldscape.server.world.entity.player.Player

var Player.playerInterest by EntityAttribute<PlayerInterest>()

class PlayerInterest {
    val localPlayers = mutableListOf<Player>()

    val externalPlayers =  mutableListOf<Player>()

    val fieldIds = IntArray(World.MAX_PLAYERS)

    val skipFlags = ByteArray(World.MAX_PLAYERS)
}