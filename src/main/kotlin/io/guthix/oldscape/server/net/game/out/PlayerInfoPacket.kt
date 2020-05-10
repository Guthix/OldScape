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

import io.guthix.buffer.*
import io.guthix.oldscape.server.dimensions.tiles
import io.guthix.oldscape.server.api.Huffman
import io.guthix.oldscape.server.dimensions.floors
import io.guthix.oldscape.server.net.game.OutGameEvent
import io.guthix.oldscape.server.net.game.VarShortSize
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.PlayerList
import io.guthix.oldscape.server.world.entity.CharacterVisual
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.entity.interest.PlayerInterestManager
import io.guthix.oldscape.server.world.entity.interest.regionId
import io.guthix.oldscape.server.world.map.Tile
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import java.util.*
import kotlin.math.abs

class PlayerInfoPacket(
    private val worldPlayers: PlayerList,
    private val player: Player
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
        player.visualInterestManager.localPlayerCount = 0
        player.visualInterestManager.externalPlayerCount = 0
        for (index in 1 until World.MAX_PLAYERS) {
           player.visualInterestManager.skipFlags[index] = (player.visualInterestManager.skipFlags[index].toInt() shr 1).toByte()
            if (player.visualInterestManager.localPlayers[index] != null) {
                player.visualInterestManager.localPlayerIndexes[player.visualInterestManager.localPlayerCount++] = index
            } else {
                player.visualInterestManager.externalPlayerIndexes[player.visualInterestManager.externalPlayerCount++] = index
            }
        }
        mainBuf.writeBytes(maskBuf)
        maskBuf.release()
        return mainBuf
    }


    private fun processLocalPlayers(buf: BitBuf, maskBuf: ByteBuf, nsn: Boolean) {
        //TODO logout
        fun localUpdateRequired(player: Player, localPlayer: Player) = (localPlayer.visualInterestManager.updateFlags.isNotEmpty()
            || !player.isInterestedIn(localPlayer)
            || localPlayer.visualInterestManager.movementType != CharacterVisual.MovementUpdateType.STAY
            )

        fun updateLocalPlayer(localPlayer: Player, bitBuf: BitBuf, maskBuf: ByteBuf) {
            val flagUpdateRequired = localPlayer.visualInterestManager.updateFlags.isNotEmpty()
            bitBuf.writeBoolean(flagUpdateRequired)
            if (localPlayer.visualInterestManager.movementType == CharacterVisual.MovementUpdateType.TELEPORT) {
                bitBuf.writeBits(value = 3, amount = 2)
                var localPlayerOutsideView = !player.isInterestedIn(localPlayer)
                if (player == localPlayer) localPlayerOutsideView = true
                bitBuf.writeBoolean(localPlayerOutsideView)
                var dx = (localPlayer.position.x - localPlayer.visualInterestManager.lastPostion.x).value
                var dy = (localPlayer.position.y - localPlayer.visualInterestManager.lastPostion.y).value
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
                player.visualInterestManager.localPlayers[localPlayer.index] = null
            } else if (localPlayer.visualInterestManager.movementType == CharacterVisual.MovementUpdateType.WALK) {
                bitBuf.writeBits(value = 1, amount = 2)
                bitBuf.writeBits(value = getDirectionWalk(localPlayer), amount = 3)
            } else if (localPlayer.visualInterestManager.movementType == CharacterVisual.MovementUpdateType.RUN) {
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
            for (i in (currentIndex + 1) until player.visualInterestManager.localPlayerCount) {
                val nextPlayerIndex = player.visualInterestManager.localPlayerIndexes[i]
                if (hasBeenSkippedLastTick(nextPlayerIndex, nsn)) {
                    val nextPlayer = player.visualInterestManager.localPlayers[nextPlayerIndex]
                    val updateRequired = nextPlayer != null && localUpdateRequired(player, nextPlayer)
                    if (updateRequired) break
                    skip++
                }
            }
            bitBuf.writeSkip(skip)
        }


        skip = 0
        for (i in 0 until player.visualInterestManager.localPlayerCount) {
            val localPlayerIndex = player.visualInterestManager.localPlayerIndexes[i]
            if (hasBeenSkippedLastTick(localPlayerIndex, nsn)) {
                if (skip > 0) {
                    skip--
                    markPlayerAsSkipped(localPlayerIndex)
                } else {
                    val localPlayer = player.visualInterestManager.localPlayers[localPlayerIndex]
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
                || player.visualInterestManager.fieldIds[externalPlayer.index] != externalPlayer.position.regionId

        fun updateField(buf: BitBuf, externalPlayer: Player) {
            val lastFieldId = player.visualInterestManager.fieldIds[externalPlayer.index]
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
           player.visualInterestManager.fieldIds[externalPlayer.index] = curentFieldId
        }

        fun updateExternalPlayer(buf: BitBuf, maskBuf: ByteBuf, externalPlayer: Player) {
            if (player.isInterestedIn(externalPlayer)) {
                buf.writeBits(value = 0, amount = 2)
                if (player.visualInterestManager.fieldIds[externalPlayer.index] != externalPlayer.position.regionId) {
                    buf.writeBoolean(true)
                    updateField(buf, externalPlayer)
                } else {
                    buf.writeBoolean(false)
                }
                buf.writeBits(value = externalPlayer.position.x.value, amount = 13)
                buf.writeBits(value = externalPlayer.position.y.value, amount = 13)
                buf.writeBoolean(true)
                updateLocalPlayerVisual(externalPlayer, maskBuf, sortedSetOf(appearance, orientation, movementCached))
                player.visualInterestManager.localPlayers[externalPlayer.index] = externalPlayer
            } else {
                updateField(buf, externalPlayer)
            }
        }

        fun skipExternalPlayers(buf: BitBuf, currentIndex: Int, nsn: Boolean) {
            for (i in (currentIndex + 1) until player.visualInterestManager.externalPlayerCount) {
                val externalPlayerIndex = player.visualInterestManager.externalPlayerIndexes[i]
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
        for (i in 0 until player.visualInterestManager.externalPlayerCount) {
            val externalPlayerIndex = player.visualInterestManager.externalPlayerIndexes[i]
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
        val dx = localPlayer.position.x - localPlayer.visualInterestManager.lastPostion.x
        val dy = localPlayer.position.y - localPlayer.visualInterestManager.lastPostion.y
        return getDirectionType(dx.value, dy.value)
    }

    private fun getDirectionType(dx: Int, dy: Int) = MOVEMENT[2 - dy][dx + 2]

    private fun markPlayerAsSkipped(playerIndex: Int) {
       player.visualInterestManager.skipFlags[playerIndex] = (player.visualInterestManager.skipFlags[playerIndex].toInt() or 0x2).toByte()
    }

    private fun hasBeenSkippedLastTick(playerIndex: Int, nsn: Boolean) = if (nsn) {
        player.visualInterestManager.skipFlags[playerIndex].toInt() and 0x1 == 0
    } else {
        player.visualInterestManager.skipFlags[playerIndex].toInt() and 0x1 != 0
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
        privateUpdates.addAll(localPlayer.visualInterestManager.updateFlags)
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
    ) : CharacterVisual.UpdateType(priority, mask)

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
            writeByteNEG(if(player.visualInterestManager.movementType == CharacterVisual.MovementUpdateType.TELEPORT) 127 else 0)
        }

        val shout = UpdateType(3, 0x10) { player ->
            writeStringCP1252(player.visualInterestManager.shoutMessage!!) // TODO
        }

        val spotAnim = UpdateType(10, 0x400) { player ->
            writeShortLEADD(player.visualInterestManager.spotAnimation?.id ?: 65535)
            writeInt(((player.visualInterestManager.spotAnimation?.height ?: 0) shl 16) or (player.visualInterestManager.spotAnimation?.delay ?:0))
        }

        val nameModifiers = UpdateType(12, 0x1000) { player ->
            player.visualInterestManager.nameModifiers.forEach { entry ->
                writeStringCP1252(entry)
            }
        }

        val sequence = UpdateType(2, 0x4) { player ->
            writeShort(player.visualInterestManager.sequenceId ?: 65535)
            writeByteADD(0)
        }

        val chat = UpdateType(1, 0x80) { player ->
            writeShortLEADD((player.visualInterestManager.publicMessage!!.color shl 8) or player.visualInterestManager.publicMessage!!.effect)
            writeByteSUB(player.visualInterestManager.rights)
            writeByte(0) // some boolean
            val compressed = Unpooled.compositeBuffer(2).apply {
                addComponents(true,
                    Unpooled.buffer(2).apply { writeSmallSmart(player.visualInterestManager.publicMessage!!.length) },
                    Unpooled.wrappedBuffer(Huffman.compress(player.visualInterestManager.publicMessage!!.message))
                )
            }
            writeByte(compressed.readableBytes())
            writeBytesReversedADD(compressed)
        }

        val movementCached = UpdateType(4, 0x800) { player ->
            writeByteADD(if(player.visualInterestManager.inRunMode) 2 else 1)
        }

        val hit = UpdateType(11, 0x1) { player ->
            //TODO
        }

        val movementForced = UpdateType(7, 0x100) { player ->
            //TODO
        }

        val lockTurnToCharacter = UpdateType(9, 0x40) { player ->
            if(player.visualInterestManager.interacting == null) {
                writeShort(65535)
            } else {
                player.visualInterestManager.interacting?.let {
                    writeShort(it.index + 32768)
                }
            }
        }

        val appearance = UpdateType(6, 0x8) { player ->
            val lengthIndex = writerIndex()
            writeByte(0) //place holder for length
            writeByte(player.visualInterestManager.gender.opcode)
            writeByte(if(player.visualInterestManager.isSkulled) 1 else -1)
            writeByte(player.visualInterestManager.prayerIcon)
            player.visualInterestManager.equipment.head?.let { // write head gear
                writeShort(512 + it.blueprint.id)
            } ?: run { writeByte(0) }
            player.visualInterestManager.equipment.cape?.let {  // write cape
                writeShort(512 + it.blueprint.id)
            } ?: run { writeByte(0) }
            player.visualInterestManager.equipment.neck?.let {  // write neck gear
                writeShort(512 + it.blueprint.id)
            } ?: run { writeByte(0) }
            player.visualInterestManager.equipment.weapon?.let { // write weapon
                writeShort(512 + it.blueprint.id)
            } ?: run { writeByte(0) }
            player.visualInterestManager.equipment.body?.let { // write body
                writeShort(512 + it.blueprint.id)
            } ?: run { writeShort(256 + player.visualInterestManager.style.torso) }
            player.visualInterestManager.equipment.shield?.let {  // write shield gear
                writeShort(512 + it.blueprint.id)
            } ?: run { writeByte(0) }
            player.visualInterestManager.equipment.body?.let { // write arms
                if(it.blueprint.isFullBody) writeByte(0) else writeShort(256 + player.visualInterestManager.style.arms)
            } ?: run { writeShort(256 + player.visualInterestManager.style.arms) }
            player.visualInterestManager.equipment.legs?.let { // write legs
                writeShort(512 + it.blueprint.id)
            } ?: run { writeShort(256 + player.visualInterestManager.style.legs) }
            player.visualInterestManager.equipment.head?.let { // write hair
                if(it.blueprint.coversHair) writeByte(0) else writeShort(256 + player.visualInterestManager.style.hair)
            } ?: run { writeShort(256 + player.visualInterestManager.style.hair) }
            player.visualInterestManager.equipment.hands?.let {  // write hands
                writeShort(512 + it.blueprint.id)
            } ?: run { writeShort(256 + player.visualInterestManager.style.hands) }
            player.visualInterestManager.equipment.feet?.let { // write feet
                writeShort(512 + it.blueprint.id)
            } ?: run { writeShort(256 + player.visualInterestManager.style.feet)}
            if(player.visualInterestManager.gender == PlayerInterestManager.Gender.MALE) { //write beard
                player.visualInterestManager.equipment.head?.let {
                    if(it.blueprint.coversFace) writeByte(0) else writeShort(256 + player.visualInterestManager.style.beard)
                } ?: run { writeShort(256 + player.visualInterestManager.style.beard) }
            } else {
                writeByte(0)
            }
            writeByte(player.visualInterestManager.colours.hair)
            writeByte(player.visualInterestManager.colours.torso)
            writeByte(player.visualInterestManager.colours.legs)
            writeByte(player.visualInterestManager.colours.feet)
            writeByte(player.visualInterestManager.colours.skin)

            writeShort(player.visualInterestManager.animations.stand)
            writeShort(player.visualInterestManager.animations.turn)
            writeShort(player.visualInterestManager.animations.walk)
            writeShort(player.visualInterestManager.animations.turn180)
            writeShort(player.visualInterestManager.animations.turn90CW)
            writeShort(player.visualInterestManager.animations.turn90CCW)
            writeShort(player.visualInterestManager.animations.run)
            writeStringCP1252(player.username) // username
            writeByte(player.visualInterestManager.combatLevel) // combat level
            writeShort(0) // skillId level
            writeByte(0) // hidden
            setByteNEG(lengthIndex, writerIndex() - lengthIndex - 1)
        }

        val orientation = UpdateType(5, 0x20) { player ->
            writeShortLEADD(player.orientation)
        }
    }
}