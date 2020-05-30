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
package io.guthix.oldscape.server.world.entity.interest

import io.guthix.oldscape.server.dimensions.tiles
import io.guthix.oldscape.server.event.PublicMessageEvent
import io.guthix.oldscape.server.net.game.out.PlayerInfoPacket
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.*
import io.guthix.oldscape.server.world.map.Tile
import io.netty.channel.ChannelFuture
import java.util.*

class PlayerManager(val index: Int) : InterestManager {
    var localPlayerCount = 0

    val localPlayers = arrayOfNulls<Player>(World.MAX_PLAYERS)

    val localPlayerIndexes = IntArray(World.MAX_PLAYERS)

    var externalPlayerCount = 0

    val externalPlayerIndexes = IntArray(World.MAX_PLAYERS)

    val regionIds = IntArray(World.MAX_PLAYERS)

    val skipFlags = ByteArray(World.MAX_PLAYERS)

    override fun initialize(world: World, player: Player) {
        localPlayers[index] = player
        localPlayerIndexes[localPlayerCount++] = index
        for (playerIndex in 1 until World.MAX_PLAYERS) {
            if (index != playerIndex) {
                val externalPlayer = world.players[playerIndex]
                regionIds[playerIndex] = externalPlayer?.pos?.regionId ?: 0
                externalPlayerIndexes[externalPlayerCount++] = playerIndex
            }
        }
    }

    override fun synchronize(world: World, player: Player): List<ChannelFuture> {
        return listOf(player.ctx.write(PlayerInfoPacket(world.players, this, player.playerVisual)))
    }

    override fun postProcess() { }

    class Equipment(
        head: HeadEquipment?,
        cape: CapeEquipment?,
        neck: NeckEquipment?,
        ammunition: AmmunitionEquipment?,
        weapon: WeaponEquipment?,
        body: BodyEquipment?,
        shield: ShieldEquipment?,
        legs: LegsEquipment?,
        hands: HandEquipment?,
        feet: FeetEquipment?,
        ring: RingEquipment?
    ) {
        var head = head
            internal set

        var cape = cape
            internal set

        var neck = neck
            internal set

        var ammunition = ammunition
            internal set

        var weapon = weapon
            internal set

        var body = body
            internal set

        var shield = shield
            internal set

        var legs = legs
            internal set

        var hands = hands
            internal set

        var feet = feet
            internal set

        var ring = ring
            internal set
    }

    data class Style(
        val hair: Int,
        val beard: Int,
        val torso: Int,
        val arms: Int,
        val legs: Int,
        val hands: Int,
        val feet: Int
    )

    data class Colours(
        var hair: Int,
        var torso: Int,
        var legs: Int,
        var feet: Int,
        var skin: Int
    )

    data class Animations(
        var stand: Int,
        var turn: Int,
        var walk: Int,
        var turn180: Int,
        var turn90CW: Int,
        var turn90CCW: Int,
        var run: Int
    )

    enum class Gender(val opcode: Int) { MALE(0), FEMALE(1) }

    companion object {
        val SIZE = 32.tiles

        val RANGE = SIZE / 2.tiles

        val REGION_SIZE = 8192.tiles

        const val MESSAGE_DURATION = 4
    }
}

val Tile.regionId get() = (floor.value shl 16) or ((x / PlayerManager.REGION_SIZE).value shl 8) or
    (y / PlayerManager.REGION_SIZE).value