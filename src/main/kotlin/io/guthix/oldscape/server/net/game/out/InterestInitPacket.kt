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
package io.guthix.oldscape.server.net.game.out

import io.guthix.buffer.toBitMode
import io.guthix.oldscape.server.dimensions.ZoneUnit
import io.guthix.oldscape.server.net.game.OutGameEvent
import io.guthix.oldscape.server.net.game.VarShortSize
import io.guthix.oldscape.server.world.PlayerList
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.entity.interest.regionId
import io.guthix.oldscape.server.world.map.Tile
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import kotlin.math.ceil

class InterestInitPacket(
    private val playersInWorld: PlayerList,
    private val player: Player,
    private val xteas: List<IntArray>,
    private val x: ZoneUnit,
    private val y: ZoneUnit
) : OutGameEvent {
    override val opcode: Int = 73

    override val size: VarShortSize = VarShortSize

    override fun encode(ctx: ChannelHandlerContext): ByteBuf {
        val bitBuf = player.ctx.alloc().buffer(STATIC_SIZE).toBitMode()
        bitBuf.writeBits(player.pos.bitpack, 30)
        for (playerIndex in 1 until World.MAX_PLAYERS) {
            val externalPlayer = playersInWorld[playerIndex]
            if (playerIndex != player.index) {
                bitBuf.writeBits(externalPlayer?.pos?.regionId ?: 0, 18)
            }
        }
        val gpiInitBuf = bitBuf.toByteMode()
        val mapInitBuf = RebuildNormalPacket(xteas, x, y).encode(ctx)
        return ctx.alloc().compositeBuffer(2).addComponents(true, gpiInitBuf, mapInitBuf)
    }

    companion object {
        private val Tile.bitpack get() = (floor.value shl 28) or (x.value shl 14) or y.value

        val STATIC_SIZE: Int
            get() = ceil((30 + (World.MAX_PLAYERS - 2) * 18).toDouble() / Byte.SIZE_BITS).toInt() +
                RebuildNormalPacket.STATIC_SIZE
    }
}