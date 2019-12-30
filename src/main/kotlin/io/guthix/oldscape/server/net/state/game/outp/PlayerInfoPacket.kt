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

import io.guthix.buffer.BitBuf
import io.guthix.buffer.toBitMode
import io.guthix.oldscape.server.net.state.game.OutGameEvent
import io.guthix.oldscape.server.net.state.game.VarShortSize
import io.guthix.oldscape.server.world.entity.character.Character
import io.guthix.oldscape.server.world.entity.character.player.Player
import io.guthix.oldscape.server.world.entity.character.player.interest.PlayerInterest
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import io.guthix.oldscape.server.world.mapsquare.zone.tile.TileUnit
import io.guthix.oldscape.server.world.mapsquare.zone.tile.tile
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import kotlin.math.abs

class PlayerInfoPacket(
    private val player: Player,
    private val interest: PlayerInterest
) : OutGameEvent() {
    override val opcode = 41

    override val size = VarShortSize

    val currentLocalActivePlayers = mutableListOf<Player>()

    val currentLocalInActivePlayers = mutableListOf<Player>()

    val currentExternalActivePlayers = mutableListOf<Player>()

    val currentExternalInActivePlayers = mutableListOf<Player>()

    override fun encode(ctx: ChannelHandlerContext): ByteBuf {
        val mainBuf = ctx.alloc().buffer()
        val maskBuf = ctx.alloc().buffer()
        mainBuf.encodeLocalPlayers(player, interest.lastLocalActivePlayers, maskBuf)
        mainBuf.encodeLocalPlayers(player, interest.lastLocalInActivePlayers, maskBuf)
        mainBuf.encodeExternalPlayers(player, interest.lastExternalActivePlayers, maskBuf)
        mainBuf.encodeExternalPlayers(player, interest.lastExternalInActivePlayers, maskBuf)
        interest.lastLocalActivePlayers = currentLocalActivePlayers
        interest.lastLocalInActivePlayers = currentLocalInActivePlayers
        interest.lastExternalActivePlayers = currentLocalActivePlayers
        interest.lastExternalInActivePlayers = currentLocalInActivePlayers
        return Unpooled.compositeBuffer(2).addComponents(true, mainBuf, maskBuf)
    }

    //TODO check for player == localPlayer?
    private fun ByteBuf.encodeLocalPlayers(player: Player, localPlayers: List<Player>, maskBuf: ByteBuf): ByteBuf {
        fun Player.updateRequiredFor(otherPlayer: Player) = updateFlags.isNotEmpty()
            || !player.isInterestedIn(otherPlayer)
            || otherPlayer.interestMovementUpdateType != Character.MovementUpdateType.STAY

        fun BitBuf.encodePlayerUpdate(otherPlayer: Player) {
            val shouldUpdate = player.updateFlags.isNotEmpty()
            writeBoolean(shouldUpdate)
            when {
                player.interestMovementUpdateType == Character.MovementUpdateType.TELEPORT -> {
                    writeBits(2, 3)
                }
                !player.isInterestedIn(otherPlayer) -> {
                    writeBits(2, 0)
                    writeBoolean(false)
                }
                otherPlayer.interestMovementUpdateType == Character.MovementUpdateType.WALK -> {
                    writeBits(2, 1)
                    writeBits(3, getDirectionWalk(otherPlayer))
                }
                otherPlayer.interestMovementUpdateType == Character.MovementUpdateType.RUN -> {
                    writeBits(2, 2)
                    writeBits(4, getDirectionWalk(otherPlayer))
                }
                shouldUpdate -> {
                    writeBits(2, 0)
                }
            }
            if(shouldUpdate) updateLocalPlayerVisual(otherPlayer, maskBuf)
        }

        val bitBuf = toBitMode()
        var skipped = 0
        for(localPlayer in localPlayers) {
            if(player.updateRequiredFor(localPlayer)) {
                if(skipped != 0) {
                    bitBuf.writeBoolean(false)
                    bitBuf.writeSkip(skipped)
                    skipped = 0
                }
                bitBuf.writeBoolean(true)
                bitBuf.encodePlayerUpdate(localPlayer)
                currentLocalActivePlayers.add(localPlayer)
            } else {
                skipped++
                currentLocalInActivePlayers.add(localPlayer)
            }
        }
        if(skipped != 0) {
            bitBuf.writeBoolean(false)
            bitBuf.writeSkip(skipped)
        }
        return bitBuf.toByteMode()
    }

    private fun ByteBuf.encodeExternalPlayers(player: Player, externalPlayers: List<Player>, maskBuf: ByteBuf): ByteBuf {
        fun Player.updateRequiredFor(otherPlayer: Player) = isInterestedIn(otherPlayer)

        fun BitBuf.encodeRegionUpdate(externalPlayer: Player) {
            val dx = externalPlayer.position.x.inRegions - externalPlayer.lastPostion.x.inRegions
            val dy = externalPlayer.position.y.inRegions - externalPlayer.lastPostion.y.inRegions
            val dz = (externalPlayer.position.z - externalPlayer.lastPostion.z).value
            when {
                dx == 0 && dy == 0 -> {
                    writeBits(2, 1)
                    writeBits(2, dz)
                }
                abs(dx) <= 1 && abs(dy) <= 1 -> {
                    writeBits(2, 2)
                    writeBits(5, (dz shl 3) or getDirectionType(dx, dy))
                }
                else -> {
                    writeBits(2, 3)
                    writeBits(18, regionId(dz, dx, dy))
                }
            }
        }

        fun BitBuf.encodePlayerUpdate(externalPlayer: Player) {
            val shouldUpdate = player.updateFlags.isNotEmpty()
            writeBoolean(shouldUpdate)
            when {
                player.isInterestedIn(externalPlayer) -> {
                    writeBits(2, 0)
                    if(externalPlayer.position.regionId == externalPlayer.lastPostion.regionId) {
                        writeBoolean(false)
                    } else {
                        writeBoolean(true)
                        encodePlayerUpdate(externalPlayer)
                    }
                    writeBits(13, externalPlayer.position.x.value)
                    writeBits(13, externalPlayer.position.y.value)
                    writeBoolean(true)
                    updateLocalPlayerVisual(externalPlayer, maskBuf)
                }
                else -> encodeRegionUpdate(externalPlayer)
            }
        }

        val bitBuf = toBitMode()
        var skipped = 0
        for(externalPlayer in externalPlayers) {
            if(player.updateRequiredFor(externalPlayer)) {
                if(skipped != 0) {
                    bitBuf.writeBoolean(false)
                    bitBuf.writeSkip(skipped)
                    skipped = 0
                }
                bitBuf.writeBoolean(true)
                bitBuf.encodePlayerUpdate(externalPlayer)
                currentExternalActivePlayers.add(externalPlayer)
            } else {
                skipped++
                currentExternalInActivePlayers.add(externalPlayer)
            }
        }
        if(skipped != 0) {
            bitBuf.writeBoolean(false)
            bitBuf.writeSkip(skipped)
        }
        return bitBuf.toByteMode()
    }

    abstract class UpdateType(
        mask: Int,
        internal val encode: ByteBuf.(player: Player) -> Unit
    ) : Character.UpdateType(mask)

    private fun updateLocalPlayerVisual(
        otherPlayer: Player,
        maskBuf: ByteBuf,
        extraFlags: MutableSet<UpdateType> = mutableSetOf()
    ) {
        extraFlags.addAll(otherPlayer.updateFlags)
        var mask = 0
        extraFlags.forEach { update ->
            mask = mask or update.mask
        }
        if (mask >= 0xFF) {
            maskBuf.writeByte(mask or 0x80)
            maskBuf.writeByte(mask shr 8)
        } else {
            maskBuf.writeByte(mask)
        }
        extraFlags.forEach { updateType ->
            updateType.encode(maskBuf, otherPlayer)
        }
    }

    companion object {
        private val INTEREST_SIZE = 32.tile

        private val INTEREST_RANGE = INTEREST_SIZE / 2.tile

        //TODO add logout
        private fun Player.isInterestedIn(player: Player) = position.withInDistanceOf(player.position, INTEREST_RANGE)

        private fun BitBuf.writeSkip(amount: Int) {
            when {
                amount == 0 -> {
                    writeBits(2, 0)
                }
                amount < 32 -> {
                    writeBits(2, 1)
                    writeBits(5, amount)
                }
                amount < 256 -> {
                    writeBits(2, 2)
                    writeBits(8, amount)
                }
                amount < 2048 -> {
                    writeBits(2, 3)
                    writeBits(11, amount)
                }
            }
        }

        private fun getDirectionWalk(localPlayer: Player): Int {
            val dx = localPlayer.position.x - localPlayer.lastPostion.x
            val dy = localPlayer.position.y - localPlayer.lastPostion.y
            return getDirectionType(dx.value, dy.value)
        }

        private fun getDirectionType(dx: Int, dy: Int) = MOVEMENT[2 - dy][dx + 2]

        private val MOVEMENT = arrayOf(
            intArrayOf(11, 12, 13, 14, 15),
            intArrayOf(9, 5, 6, 7, 10),
            intArrayOf(7, 3, -1, 4, 8),
            intArrayOf(5, 0, 1, 2, 6),
            intArrayOf(0, 1, 2, 3, 4)
        )

        private val REGION_SIZE = 8192.tile

        private val TileUnit.inRegions get() = (this / REGION_SIZE).value

        private fun regionId(floor: Int, regionX: Int, regionY: Int) = (floor shl 16) or ((regionX shl 8) or regionY)

        private val Tile.regionId get() = (z.value shl 16) or ((x / REGION_SIZE).value shl 8) or (y / REGION_SIZE).value
    }
}