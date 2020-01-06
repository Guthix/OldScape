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
package io.guthix.oldscape.server.world.entity.character.player.interest

import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.character.player.Player
import io.guthix.oldscape.server.world.entity.character.player.PlayerList
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import io.guthix.oldscape.server.world.mapsquare.zone.tile.tile

class PlayerInterest {
    var localPlayerCount = 0

    val localPlayers = arrayOfNulls<Player>(World.MAX_PLAYERS)

    val localPlayerIndexes = IntArray(World.MAX_PLAYERS)

    var externalPlayerCount = 0

    val externalPlayerIndexes = IntArray(World.MAX_PLAYERS)

    val fieldIds = IntArray(World.MAX_PLAYERS)

    val skipFlags = ByteArray(World.MAX_PLAYERS)

    fun initialize(player: Player, worldPlayers: PlayerList) {
        localPlayers[player.index] = player
        localPlayerIndexes[localPlayerCount++] = player.index
        for (playerIndex in 1 until World.MAX_PLAYERS) {
            if (player.index != playerIndex) {
                val externalPlayer = worldPlayers[playerIndex]
                fieldIds[playerIndex] = externalPlayer?.position?.regionId ?: 0
                externalPlayerIndexes[externalPlayerCount++] = playerIndex
            }
        }
    }

    companion object {
        val SIZE = 32.tile

        val RANGE = SIZE / 2.tile

        private val REGION_SIZE = 8192.tile

        val Tile.regionId get() = (z.value shl 16) or ((x / REGION_SIZE).value shl 8) or (y / REGION_SIZE).value
    }
}