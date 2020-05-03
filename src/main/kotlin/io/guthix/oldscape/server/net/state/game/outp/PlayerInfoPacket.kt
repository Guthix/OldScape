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
package io.guthix.oldscape.server.net.state.game.outp

import io.guthix.buffer.*
import io.guthix.oldscape.server.dimensions.floors
import io.guthix.oldscape.server.dimensions.tiles
import io.guthix.oldscape.server.api.Huffman
import io.guthix.oldscape.server.net.state.game.OutGameEvent
import io.guthix.oldscape.server.net.state.game.VarShortSize
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.character.player.interest.PlayerInterestManager.Companion.regionId
import io.guthix.oldscape.server.world.entity.character.Character
import io.guthix.oldscape.server.world.entity.character.player.Appearance
import io.guthix.oldscape.server.world.entity.character.player.Player
import io.guthix.oldscape.server.world.entity.character.player.PlayerList
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import java.util.*
import kotlin.math.abs

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
        player.playerInterestManager.localPlayerCount = 0
        player.playerInterestManager.externalPlayerCount = 0
        for (index in 1 until World.MAX_PLAYERS) {
            player.playerInterestManager.skipFlags[index] = (player.playerInterestManager.skipFlags[index].toInt() shr 1).toByte()
            if (player.playerInterestManager.localPlayers[index] != null) {
                player.playerInterestManager.localPlayerIndexes[player.playerInterestManager.localPlayerCount++] = index
            } else {
                player.playerInterestManager.externalPlayerIndexes[player.playerInterestManager.externalPlayerCount++] = index
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
                        ((localPlayer.position.floor.value and 0x3) shl 28) or ((dx and 0x3fff) shl 14) or (dy and 0x3fff),
                        30
                    )
                } else {
                    if (dx < 0) dx += 32
                    if (dy < 0) dy += 32
                    buf.writeBits(
                        ((localPlayer.position.floor.value and 0x3) shl 10) or ((dx and 0x1F) shl 5) or (dy and 0x1F),
                        12
                    )
                }
            } else if (!player.isInterestedIn(localPlayer)) {
                bitBuf.writeBits(value = 0, amount = 2)
                bitBuf.writeBoolean(false)
                player.playerInterestManager.localPlayers[localPlayer.index] = null
            } else if (localPlayer.movementType == Character.MovementUpdateType.WALK) {
                bitBuf.writeBits(value = 1, amount = 2)
                bitBuf.writeBits(value = getDirectionWalk(localPlayer), amount = 3)
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
            for (i in (currentIndex + 1) until player.playerInterestManager.localPlayerCount) {
                val nextPlayerIndex = player.playerInterestManager.localPlayerIndexes[i]
                if (hasBeenSkippedLastTick(nextPlayerIndex, nsn)) {
                    val nextPlayer = player.playerInterestManager.localPlayers[nextPlayerIndex]
                    val updateRequired = nextPlayer != null && localUpdateRequired(player, nextPlayer)
                    if (updateRequired) break
                    skip++
                }
            }
            bitBuf.writeSkip(skip)
        }


        skip = 0
        for (i in 0 until player.playerInterestManager.localPlayerCount) {
            val localPlayerIndex = player.playerInterestManager.localPlayerIndexes[i]
            if (hasBeenSkippedLastTick(localPlayerIndex, nsn)) {
                if (skip > 0) {
                    skip--
                    markPlayerAsSkipped(localPlayerIndex)
                } else {
                    val localPlayer = player.playerInterestManager.localPlayers[localPlayerIndex]
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
                || player.playerInterestManager.fieldIds[externalPlayer.index] != externalPlayer.position.regionId

        fun updateField(buf: BitBuf, externalPlayer: Player) {
            val lastFieldId = player.playerInterestManager.fieldIds[externalPlayer.index]
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
            } else if (abs(dx) <= 1 && abs(dy) <= 1) {
                buf.writeBits(value = 2, amount = 2)
                buf.writeBits(value = (dz shl 3) or getDirectionType(dx, dy), amount = 5)
            } else {
                buf.writeBits(value = 3, amount = 2)
                buf.writeBits(value = Tile(dz.floors, dx.tiles, dy.tiles).regionId, amount = 18)
            }
            player.playerInterestManager.fieldIds[externalPlayer.index] = curentFieldId
        }

        fun updateExternalPlayer(buf: BitBuf, maskBuf: ByteBuf, externalPlayer: Player) {
            if (player.isInterestedIn(externalPlayer)) {
                buf.writeBits(value = 0, amount = 2)
                if (player.playerInterestManager.fieldIds[externalPlayer.index] != externalPlayer.position.regionId) {
                    buf.writeBoolean(true)
                    updateField(buf, externalPlayer)
                } else {
                    buf.writeBoolean(false)
                }
                buf.writeBits(value = externalPlayer.position.x.value, amount = 13)
                buf.writeBits(value = externalPlayer.position.y.value, amount = 13)
                buf.writeBoolean(true)
                updateLocalPlayerVisual(externalPlayer, maskBuf, sortedSetOf(appearance, orientation, movementCached))
                player.playerInterestManager.localPlayers[externalPlayer.index] = externalPlayer
            } else {
                updateField(buf, externalPlayer)
            }
        }

        fun skipExternalPlayers(buf: BitBuf, currentIndex: Int, nsn: Boolean) {
            for (i in (currentIndex + 1) until player.playerInterestManager.externalPlayerCount) {
                val externalPlayerIndex = player.playerInterestManager.externalPlayerIndexes[i]
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
        for (i in 0 until player.playerInterestManager.externalPlayerCount) {
            val externalPlayerIndex = player.playerInterestManager.externalPlayerIndexes[i]
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
        player.playerInterestManager.skipFlags[playerIndex] = (player.playerInterestManager.skipFlags[playerIndex].toInt() or 0x2).toByte()
    }

    private fun hasBeenSkippedLastTick(playerIndex: Int, nsn: Boolean) = if (nsn) {
        player.playerInterestManager.skipFlags[playerIndex].toInt() and 0x1 == 0
    } else {
        player.playerInterestManager.skipFlags[playerIndex].toInt() and 0x1 != 0
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
        privateUpdates: SortedSet<UpdateType> = sortedSetOf()
    ) {
        var mask = 0
        privateUpdates.addAll(localPlayer.updateFlags)
        privateUpdates.forEach { update ->
            mask = mask or update.mask
        }
        if (mask >= 0xff) {
            maskBuf.writeByte(mask or 0x2)
            maskBuf.writeByte(mask shr 8)
        } else {
            maskBuf.writeByte(mask)
        }
        privateUpdates.forEach { updateType ->
            updateType.encode(maskBuf, localPlayer)
        }
    }

    class UpdateType(
        priority: Int,
        mask: Int,
        val encode: ByteBuf.(player: Player) -> Unit
    ) : Character.UpdateType(priority, mask)

    companion object {
        private val INTEREST_SIZE = 32.tiles

        private val INTEREST_RANGE = INTEREST_SIZE / 2.tiles

        private fun Player.isInterestedIn(player: Player) = position.withInDistanceOf(player.position, INTEREST_RANGE)

        private val MOVEMENT = arrayOf(
            intArrayOf(11, 12, 13, 14, 15),
            intArrayOf(9, 5, 6, 7, 10),
            intArrayOf(7, 3, -1, 4, 8),
            intArrayOf(5, 0, 1, 2, 6),
            intArrayOf(0, 1, 2, 3, 4)
        )

        val movementTemporary = UpdateType(8, 0x200) { player ->
            writeByteNEG(if(player.movementType == Character.MovementUpdateType.TELEPORT) 127 else 0)
        }

        val shout = UpdateType(3, 0x10) { player ->
            writeStringCP1252(player.shoutMessage)
        }

        val spotAnim = UpdateType(10, 0x400) { player ->
            writeShortLEADD(player.spotAnimation?.id ?: 65535)
            writeInt(((player.spotAnimation?.height ?: 0) shl 16) or (player.spotAnimation?.delay ?:0))
        }

        val nameModifiers = UpdateType(12, 0x1000) { player ->
            player.nameModifiers.forEach { entry ->
                writeStringCP1252(entry)
            }
        }

        val sequence = UpdateType(2, 0x4) { player ->
            writeShort(player.sequenceId ?: 65535)
            writeByteADD(0)
        }

        val chat = UpdateType(1, 0x80) { player ->
            writeShortLEADD((player.publicMessage.color shl 8) or player.publicMessage.effect)
            writeByteSUB(player.rights)
            writeByte(0) // some boolean
            val compressed = Unpooled.compositeBuffer(2).apply {
                addComponents(true,
                    Unpooled.buffer(2).apply { writeSmallSmart(player.publicMessage.length) },
                    Unpooled.wrappedBuffer(Huffman.compress(player.publicMessage.message))
                )
            }
            writeByte(compressed.readableBytes())
            writeBytesReversedADD(compressed)
        }

        val movementCached = UpdateType(4, 0x800) { player ->
            writeByteADD(if(player.inRunMode) 2 else 1)
        }

        val hit = UpdateType(11, 0x1) { player ->
            //TODO
        }

        val movementForced = UpdateType(7, 0x100) { player ->
            //TODO
        }

        val lockTurnToCharacter = UpdateType(9, 0x40) { player ->
            if(player.interacting == null) {
                writeShort(65535)
            } else {
                player.interacting?.let {
                    writeShort(it.index + 32768)
                }
            }
        }

        val appearance = UpdateType(6, 0x8) { player ->
            val lengthIndex = writerIndex()
            writeByte(0) //place holder for length
            writeByte(player.appearance.gender.opcode)
            writeByte(if(player.appearance.isSkulled) 1 else -1)
            writeByte(player.appearance.prayerIcon)
            player.appearance.equipment.head?.let { // write head gear
                writeShort(512 + it.blueprint.id)
            } ?: run { writeByte(0) }
            player.appearance.equipment.cape?.let {  // write cape
                writeShort(512 + it.blueprint.id)
            } ?: run { writeByte(0) }
            player.appearance.equipment.neck?.let {  // write neck gear
                writeShort(512 + it.blueprint.id)
            } ?: run { writeByte(0) }
            player.appearance.equipment.weapon?.let { // write weapon
                writeShort(512 + it.blueprint.id)
            } ?: run { writeByte(0) }
            player.appearance.equipment.body?.let { // write body
                writeShort(512 + it.blueprint.id)
            } ?: run { writeShort(256 + player.appearance.style.torso) }
            player.appearance.equipment.shield?.let {  // write shield gear
                writeShort(512 + it.blueprint.id)
            } ?: run { writeByte(0) }
            player.appearance.equipment.body?.let { // write arms
                if(it.blueprint.isFullBody) writeByte(0) else writeShort(256 + player.appearance.style.arms)
            } ?: run { writeShort(256 + player.appearance.style.arms) }
            player.appearance.equipment.legs?.let { // write legs
                writeShort(512 + it.blueprint.id)
            } ?: run { writeShort(256 + player.appearance.style.legs) }
            player.appearance.equipment.head?.let { // write hair
                if(it.blueprint.coversHair) writeByte(0) else writeShort(256 + player.appearance.style.hair)
            } ?: run { writeShort(256 + player.appearance.style.hair) }
            player.appearance.equipment.hands?.let {  // write hands
                writeShort(512 + it.blueprint.id)
            } ?: run { writeShort(256 + player.appearance.style.hands) }
            player.appearance.equipment.feet?.let { // write feet
                writeShort(512 + it.blueprint.id)
            } ?: run { writeShort(256 + player.appearance.style.feet)}
            if(player.appearance.gender == Appearance.Gender.MALE) { //write beard
                player.appearance.equipment.head?.let {
                    if(it.blueprint.coversFace) writeByte(0) else writeShort(256 + player.appearance.style.beard)
                } ?: run { writeShort(256 + player.appearance.style.beard) }
            } else {
                writeByte(0)
            }
            writeByte(player.appearance.colours.hair)
            writeByte(player.appearance.colours.torso)
            writeByte(player.appearance.colours.legs)
            writeByte(player.appearance.colours.feet)
            writeByte(player.appearance.colours.skin)

            writeShort(player.appearance.animations.stand)
            writeShort(player.appearance.animations.turn)
            writeShort(player.appearance.animations.walk)
            writeShort(player.appearance.animations.turn180)
            writeShort(player.appearance.animations.turn90CW)
            writeShort(player.appearance.animations.turn90CCW)
            writeShort(player.appearance.animations.run)
            writeStringCP1252(player.username) // username
            writeByte(player.combatLevel) // combat level
            writeShort(0) // skillId level
            writeByte(0) // hidden
            setByteNEG(lengthIndex, writerIndex() - lengthIndex - 1)
        }

        val orientation = UpdateType(5, 0x20) { player ->
            writeShortLEADD(player.orientation)
        }
    }
}