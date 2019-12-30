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
package io.guthix.oldscape.server.net.state.game.outp

import io.guthix.buffer.toBitMode
import io.guthix.oldscape.server.net.state.game.OutGameEvent
import io.guthix.oldscape.server.net.state.game.VarShortSize
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.character.player.Player
import io.guthix.oldscape.server.world.mapsquare.zone.Zone
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import kotlin.math.ceil

class InterestInitPacket(
    private val player: Player,
    private val playersInWorld: Map<Int, Player>,
    private val xteas: List<IntArray>,
    private val zone: Zone
) : OutGameEvent() {
    override val opcode = 73

    override val size = VarShortSize

    override fun encode(ctx: ChannelHandlerContext): ByteBuf {
        val bitBuf = player.ctx.alloc().buffer(STATIC_SIZE).toBitMode()
        bitBuf.writeBits(player.position.bitpack, 30)
        for(playerIndex in 1 until World.MAX_PLAYERS) {
            val initPlayer = playersInWorld[playerIndex]
            if(playerIndex != player.index) {
                bitBuf.writeBits(initPlayer?.position?.regionBitPack ?: 0, 18)
            }
        }
        val gpiInitBuf = bitBuf.toByteMode()
        val mapInitBuf = RebuildNormalPacket(xteas, zone).encode(ctx)
        return ctx.alloc().compositeBuffer(2).addComponents(true, gpiInitBuf, mapInitBuf)
    }

    companion object {
        private val Tile.regionBitPack get() =
            (z.value shl 16) or ((x.value / FIELD_TILE_SIZE) shl 8) or (y.value / FIELD_TILE_SIZE)

        private val Tile.bitpack get() = (z.value shl 28) or (x.value shl 14) or y.value

        private const val FIELD_TILE_SIZE = 8192

        val STATIC_SIZE get() = ceil((30 + (World.MAX_PLAYERS - 2) * 18).toDouble() / Byte.SIZE_BITS).toInt() +
            RebuildNormalPacket.STATIC_SIZE
    }
}