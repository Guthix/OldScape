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

import io.guthix.buffer.*
import io.guthix.oldscape.server.net.state.game.OutGameEvent
import io.guthix.oldscape.server.net.state.game.VarShortSize
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.character.player.interest.PlayerInterest.Companion.regionId
import io.guthix.oldscape.server.world.entity.character.Character
import io.guthix.oldscape.server.world.entity.character.player.Player
import io.guthix.oldscape.server.world.entity.character.player.PlayerList
import io.guthix.oldscape.server.world.mapsquare.floor
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import io.guthix.oldscape.server.world.mapsquare.zone.tile.tile
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class PlayerInfoPacket(
    private val player: Player,
    private val worldPlayers: PlayerList
) : OutGameEvent {
    override val opcode = 41

    override val size = VarShortSize

    private var skip = 0

    override fun encode(ctx: ChannelHandlerContext): ByteBuf {
        val mainBuf = ctx.alloc().buffer()
        val maskBuf = ctx.alloc().buffer()
        processLocalPlayers(mainBuf.toBitMode(), maskBuf, true)
        processLocalPlayers(mainBuf.toBitMode(), maskBuf, false)
        processExternalPlayers(mainBuf.toBitMode(), maskBuf, false)
        processExternalPlayers(mainBuf.toBitMode(), maskBuf, true)
        player.playerInterest.localPlayerCount = 0
        player.playerInterest.externalPlayerCount = 0
        for (index in 1 until World.MAX_PLAYERS) {
            player.playerInterest.skipFlags[index] = (player.playerInterest.skipFlags[index].toInt() shr 1).toByte()
            if (player.playerInterest.localPlayers[index] != null) {
                player.playerInterest.localPlayerIndexes[player.playerInterest.localPlayerCount++] = index
            } else {
                player.playerInterest.externalPlayerIndexes[player.playerInterest.externalPlayerCount++] = index
            }
        }
        mainBuf.writeBytes(maskBuf)
        maskBuf.release()
        return mainBuf
    }


    private fun processLocalPlayers(buf: BitBuf, maskBuf: ByteBuf, nsn: Boolean) {
        //TODO logout
        fun localUpdateRequired(player: Player, localPlayer: Player) = (localPlayer.updateFlags.isNotEmpty()
            || !player.isInterestedIn(localPlayer)
            || localPlayer.movementType != Character.MovementUpdateType.STAY
            )

        fun updateLocalPlayer(localPlayer: Player, bitBuf: BitBuf, maskBuf: ByteBuf) {
            val flagUpdateRequired = localPlayer.updateFlags.isNotEmpty()
            bitBuf.writeBoolean(flagUpdateRequired)
            if (localPlayer.movementType == Character.MovementUpdateType.TELEPORT) {
                bitBuf.writeBits(value = 3, amount = 2)
                var localPlayerOutsideView = !player.isInterestedIn(localPlayer)
                if (player == localPlayer) localPlayerOutsideView = true
                bitBuf.writeBoolean(localPlayerOutsideView)
                var dx = (localPlayer.position.x - localPlayer.lastPostion.x).value
                var dy = (localPlayer.position.y - localPlayer.lastPostion.y).value
                if (localPlayerOutsideView) {
                    buf.writeBits(
                        ((localPlayer.position.z.value and 0x3) shl 28) or ((dx and 0x3fff) shl 14) or (dy and 0x3fff),
                        30
                    )
                } else {
                    if (dx < 0) dx += 32
                    if (dy < 0) dy += 32
                    buf.writeBits(
                        ((localPlayer.position.z.value and 0x3) shl 10) or ((dx and 0x1F) shl 5) or (dy and 0x1F),
                        12
                    )
                }
            } else if (!player.isInterestedIn(localPlayer)) {
                bitBuf.writeBits(value = 0, amount = 2)
                bitBuf.writeBoolean(false)
                player.playerInterest.localPlayers[localPlayer.index] = null
            } else if (localPlayer.movementType == Character.MovementUpdateType.WALK) {
                bitBuf.writeBits(value = 1, amount = 2)
                bitBuf.writeBits(3, getDirectionWalk(localPlayer))
            } else if (localPlayer.movementType == Character.MovementUpdateType.RUN) {
                bitBuf.writeBits(value = 2, amount = 2)
                bitBuf.writeBits(value = getDirectionWalk(localPlayer), amount = 4)
            } else if (flagUpdateRequired) {
                bitBuf.writeBits(value = 0, amount = 2)
            }
            if (flagUpdateRequired) {
                updateLocalPlayerVisual(localPlayer, maskBuf)
            }
        }

        fun skipLocalPlayers(bitBuf: BitBuf, currentIndex: Int, nsn: Boolean) {
            for (i in (currentIndex + 1) until player.playerInterest.localPlayerCount) {
                val nextPlayerIndex = player.playerInterest.localPlayerIndexes[i]
                if (hasBeenSkippedLastTick(nextPlayerIndex, nsn)) {
                    val nextPlayer = player.playerInterest.localPlayers[nextPlayerIndex]
                    val updateRequired = nextPlayer != null && localUpdateRequired(player, nextPlayer)
                    if (updateRequired) break
                    skip++
                }
            }
            bitBuf.writeSkip(skip)
        }


        skip = 0
        for (i in 0 until player.playerInterest.localPlayerCount) {
            val localPlayerIndex = player.playerInterest.localPlayerIndexes[i]
            if (hasBeenSkippedLastTick(localPlayerIndex, nsn)) {
                if (skip > 0) {
                    skip--
                    markPlayerAsSkipped(localPlayerIndex)
                } else {
                    val localPlayer = player.playerInterest.localPlayers[localPlayerIndex]
                    val updateRequired = localPlayer != null && localUpdateRequired(player, localPlayer)
                    buf.writeBoolean(updateRequired)
                    if (!updateRequired) {
                        skipLocalPlayers(buf, i, nsn)
                        markPlayerAsSkipped(localPlayerIndex)
                    } else {
                        updateLocalPlayer(localPlayer!!, buf, maskBuf)
                    }
                }
            }
        }
        buf.toByteMode()
    }

    private fun processExternalPlayers(buf: BitBuf, maskBuf: ByteBuf, nsn: Boolean) {
        fun externalUpdateRequired(player: Player, externalPlayer: Player) = player.isInterestedIn(externalPlayer)
                || player.playerInterest.fieldIds[externalPlayer.index] != externalPlayer.position.regionId

        fun updateField(buf: BitBuf, externalPlayer: Player) {
            val lastFieldId = player.playerInterest.fieldIds[externalPlayer.index]
            val lastFieldY = lastFieldId and 0xFF
            val lastFieldX = (lastFieldId shr 8) and 0xFF
            val lastFieldZ = lastFieldId shr 16

            val curentFieldId = externalPlayer.position.regionId
            val curentFieldY = curentFieldId and 0xFF
            val curentFieldX = (curentFieldId shr 8) and 0xFF
            val curentFieldZ = curentFieldId shr 16

            val dx = curentFieldX - lastFieldX
            val dy = curentFieldY - lastFieldY
            val dz = (curentFieldZ - lastFieldZ) and 0x3
            if (dx == 0 && dy == 0) {
                buf.writeBits(value = 1, amount = 2)
                buf.writeBits(value = dz, amount = 2)
            } else if (Math.abs(dx) <= 1 && Math.abs(dy) <= 1) {
                buf.writeBits(value = 2, amount = 2)
                buf.writeBits(value = (dz shl 3) or getDirectionType(dx, dy), amount = 5)
            } else {
                buf.writeBits(value = 3, amount = 2)
                buf.writeBits(value = Tile(dz.floor, dx.tile, dy.tile).regionId, amount = 18)
            }
            player.playerInterest.fieldIds[externalPlayer.index] = curentFieldId
        }

        fun updateExternalPlayer(buf: BitBuf, maskBuf: ByteBuf, externalPlayer: Player) {
            if (player.isInterestedIn(externalPlayer)) {
                buf.writeBits(value = 0, amount = 2)
                if (player.playerInterest.fieldIds[externalPlayer.index] != externalPlayer.position.regionId) {
                    buf.writeBoolean(true)
                    updateField(buf, externalPlayer)
                } else {
                    buf.writeBoolean(false)
                }
                buf.writeBits(value = externalPlayer.position.x.value, amount = 13)
                buf.writeBits(value = externalPlayer.position.y.value, amount = 13)
                buf.writeBoolean(true)
                updateLocalPlayerVisual(externalPlayer, maskBuf, mutableSetOf(appearance))
                player.playerInterest.localPlayers[externalPlayer.index] = externalPlayer
            } else {
                updateField(buf, externalPlayer)
            }
        }

        fun skipExternalPlayers(buf: BitBuf, currentIndex: Int, nsn: Boolean) {
            for (i in (currentIndex + 1) until player.playerInterest.externalPlayerCount) {
                val externalPlayerIndex = player.playerInterest.externalPlayerIndexes[i]
                if (hasBeenSkippedLastTick(externalPlayerIndex, nsn)) {
                    val nextPlayer = worldPlayers[externalPlayerIndex]
                    val requiresFlagUpdates = nextPlayer != null && externalUpdateRequired(player, nextPlayer)
                    if (requiresFlagUpdates) {
                        break
                    }
                    skip++
                }
            }
            buf.writeSkip(skip)
        }

        skip = 0
        for (i in 0 until player.playerInterest.externalPlayerCount) {
            val externalPlayerIndex = player.playerInterest.externalPlayerIndexes[i]
            if (hasBeenSkippedLastTick(externalPlayerIndex, nsn)) {
                if (skip > 0) {
                    skip--
                    markPlayerAsSkipped(externalPlayerIndex)
                } else {
                    val externalPlayer = worldPlayers[externalPlayerIndex]
                    val updateRequired = externalPlayer != null && externalUpdateRequired(player, externalPlayer)
                    buf.writeBoolean(updateRequired)
                    if (!updateRequired) {
                        skipExternalPlayers(buf, i, nsn)
                        markPlayerAsSkipped(externalPlayerIndex)
                    } else {
                        updateExternalPlayer(buf, maskBuf, externalPlayer!!)
                        markPlayerAsSkipped(externalPlayerIndex)
                    }
                }
            }
        }
        buf.toByteMode()
    }

    private fun getDirectionWalk(localPlayer: Player): Int {
        val dx = localPlayer.position.x - localPlayer.lastPostion.x
        val dy = localPlayer.position.y - localPlayer.lastPostion.y
        return getDirectionType(dx.value, dy.value)
    }

    private fun getDirectionType(dx: Int, dy: Int) = MOVEMENT[2 - dy][dx + 2]

    private fun markPlayerAsSkipped(playerIndex: Int) {
        player.playerInterest.skipFlags[playerIndex] = (player.playerInterest.skipFlags[playerIndex].toInt() or 0x2).toByte()
    }

    private fun hasBeenSkippedLastTick(playerIndex: Int, nsn: Boolean) = if (nsn) {
        player.playerInterest.skipFlags[playerIndex].toInt() and 0x1 == 0
    } else {
        player.playerInterest.skipFlags[playerIndex].toInt() and 0x1 != 0
    }

    private fun BitBuf.writeSkip(amount: Int) {
        when {
            amount == 0 -> {
                writeBits(value = 0, amount = 2)
            }
            amount < 32 -> {
                writeBits(value = 1, amount = 2)
                writeBits(value = amount, amount = 5)
            }
            amount < 256 -> {
                writeBits(value = 2, amount = 2)
                writeBits(value = amount, amount = 8)
            }
            amount < 2048 -> {
                writeBits(value = 3, amount = 2)
                writeBits(value = amount, amount = 11)
            }
        }
    }

    private fun updateLocalPlayerVisual(
        localPlayer: Player,
        maskBuf: ByteBuf,
        privateUpdates: MutableSet<UpdateType> = mutableSetOf()
    ) {
        var mask = 0
        println("Update player visual")
        privateUpdates.addAll(localPlayer.updateFlags)
        privateUpdates.forEach { update ->
            mask = mask or update.mask
        }
        if (mask >= 0xff) {
            maskBuf.writeByte(mask or 0x80)
            maskBuf.writeByte(mask shr 8)
        } else {
            maskBuf.writeByte(mask)
        }
        privateUpdates.forEach { updateType ->
            updateType.encode(maskBuf, localPlayer)
        }
    }

    class UpdateType(mask: Int, val encode: ByteBuf.(player: Player) -> Unit) : Character.UpdateType(mask)

    companion object {
        private val INTEREST_SIZE = 32.tile

        private val INTEREST_RANGE = INTEREST_SIZE / 2.tile

        private fun Player.isInterestedIn(player: Player) = position.withInDistanceOf(player.position, INTEREST_RANGE)

        private val MOVEMENT = arrayOf(
            intArrayOf(11, 12, 13, 14, 15),
            intArrayOf(9, 5, 6, 7, 10),
            intArrayOf(7, 3, -1, 4, 8),
            intArrayOf(5, 0, 1, 2, 6),
            intArrayOf(0, 1, 2, 3, 4)
        )

        val movementTemporary = UpdateType(0x200) { player ->
            //TODO
        }

        val shout = UpdateType(0x8) { _ ->
            //TODO
        }

        val graphic = UpdateType(0x800) { player ->
            //TODO
        }

        val nameModification = UpdateType(0x400) { player ->
            //TODO
        }

        val animation = UpdateType(0x20) { player ->
            //TODO
        }

        val chat = UpdateType(0x40) { player ->
            //TODO
        }

        val movementCached = UpdateType(0x1000) { player ->
            //TODO
        }

        val hit = UpdateType(0x1) { player ->
            //TODO
        }

        val movementForced = UpdateType(0x100) { player ->
            //TODO
        }

        val lockTurnToCharacter = UpdateType(0x10) { player ->
            //TODO
        }

        val appearance = UpdateType(0x8) { player ->
            val lengthIndex = writerIndex()
            writeByte(0) //place holder for length
            writeByte(0) // gender 0 for male 1 for female
            writeByte(-1) // isSkulled 1 for skull
            writeByte(-1) // overhead icon

            for (i in 0 until 4) {
                writeByte(0)
            }
            writeShort(0x100 + 18) // chest
            writeByte(0) // shield
            writeShort(0x100 + 26) // full body
            writeShort(0x100 + 36) // legs
            writeShort(0x100 + 0) // hat
            writeShort(0x100 + 33) // hands
            writeShort(0x100 + 42) // feet
            writeShort(0x100 + 11)

            // originalColors
            for (i in 0 until 5) {
                writeByte(0)
            }

            writeShort(808) // stand anim
            writeShort(823) // stand turn
            writeShort(819) // walk anim
            writeShort(820) // turn 180
            writeShort(821) // turn 90 cw
            writeShort(822) // turn 90 ccw
            writeShort(824) // run anim
            println(player.username)
            writeStringCP1252(player.username) // username
            writeByte(126) // combat level
            writeShort(0) // skillId level
            writeByte(0) // hidden
            setByteNEG(lengthIndex, writerIndex() - lengthIndex - 1)
        }

        val orientation = UpdateType(0x2) { player ->
            // TODO
        }
    }
}