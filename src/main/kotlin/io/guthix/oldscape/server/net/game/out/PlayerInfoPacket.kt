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
import io.guthix.oldscape.server.world.entity.interest.PlayerManager
import io.guthix.oldscape.server.world.entity.interest.regionId
import io.guthix.oldscape.server.world.map.Tile
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import java.util.*
import kotlin.math.abs

class PlayerInfoPacket(
    private val worldPlayers: PlayerList,
    private val im: PlayerManager
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
        im.localPlayerCount = 0
        im.externalPlayerCount = 0
        for (index in 1 until World.MAX_PLAYERS) {
           im.skipFlags[index] = (im.skipFlags[index].toInt() shr 1).toByte()
            if (im.localPlayers[index] != null) {
                im.localPlayerIndexes[im.localPlayerCount++] = index
            } else {
                im.externalPlayerIndexes[im.externalPlayerCount++] = index
            }
        }
        mainBuf.writeBytes(maskBuf)
        maskBuf.release()
        return mainBuf
    }


    private fun processLocalPlayers(buf: BitBuf, maskBuf: ByteBuf, nsn: Boolean) {
        //TODO logout
        fun localUpdateRequired(im: PlayerManager, localPlayer: Player) = (this.im.updateFlags.isNotEmpty()
            || !im.position.isInterestedIn(localPlayer.pos)
            || this.im.movementType != CharacterVisual.MovementUpdateType.STAY
            )

        fun updateLocalPlayer(localPlayer: Player, bitBuf: BitBuf, maskBuf: ByteBuf) {
            val flagUpdateRequired = im.updateFlags.isNotEmpty()
            bitBuf.writeBoolean(flagUpdateRequired)
            if (im.movementType == CharacterVisual.MovementUpdateType.TELEPORT) {
                bitBuf.writeBits(value = 3, amount = 2)
                var localPlayerOutsideView = !im.position.isInterestedIn(localPlayer.pos)
                if (im.index == localPlayer.index) localPlayerOutsideView = true
                bitBuf.writeBoolean(localPlayerOutsideView)
                var dx = (localPlayer.pos.x - im.lastPostion.x).value
                var dy = (localPlayer.pos.y - im.lastPostion.y).value
                if (localPlayerOutsideView) {
                    buf.writeBits(
                        ((localPlayer.pos.floor.value and 0x3) shl 28) or ((dx and 0x3fff) shl 14) or (dy and 0x3fff),
                        30
                    )
                } else {
                    if (dx < 0) dx += 32
                    if (dy < 0) dy += 32
                    buf.writeBits(
                        ((localPlayer.pos.floor.value and 0x3) shl 10) or ((dx and 0x1F) shl 5) or (dy and 0x1F),
                        12
                    )
                }
            } else if (!im.position.isInterestedIn(localPlayer.pos)) {
                bitBuf.writeBits(value = 0, amount = 2)
                bitBuf.writeBoolean(false)
                im.localPlayers[localPlayer.index] = null
            } else if (im.movementType == CharacterVisual.MovementUpdateType.WALK) {
                bitBuf.writeBits(value = 1, amount = 2)
                bitBuf.writeBits(value = getDirectionWalk(localPlayer), amount = 3)
            } else if (im.movementType == CharacterVisual.MovementUpdateType.RUN) {
                bitBuf.writeBits(value = 2, amount = 2)
                bitBuf.writeBits(value = getDirectionWalk(localPlayer), amount = 4)
            } else if (flagUpdateRequired) {
                bitBuf.writeBits(value = 0, amount = 2)
            }
            if (flagUpdateRequired) {
                updateLocalPlayerVisual(localPlayer.visualManager, maskBuf)
            }
        }

        fun skipLocalPlayers(bitBuf: BitBuf, currentIndex: Int, nsn: Boolean) {
            for (i in (currentIndex + 1) until im.localPlayerCount) {
                val nextPlayerIndex = im.localPlayerIndexes[i]
                if (hasBeenSkippedLastTick(nextPlayerIndex, nsn)) {
                    val nextPlayer = im.localPlayers[nextPlayerIndex]
                    val updateRequired = nextPlayer != null && localUpdateRequired(im, nextPlayer)
                    if (updateRequired) break
                    skip++
                }
            }
            bitBuf.writeSkip(skip)
        }


        skip = 0
        for (i in 0 until im.localPlayerCount) {
            val localPlayerIndex = im.localPlayerIndexes[i]
            if (hasBeenSkippedLastTick(localPlayerIndex, nsn)) {
                if (skip > 0) {
                    skip--
                    markPlayerAsSkipped(localPlayerIndex)
                } else {
                    val localPlayer = im.localPlayers[localPlayerIndex]
                    val updateRequired = localPlayer != null && localUpdateRequired(im, localPlayer)
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
        fun externalUpdateRequired(player: PlayerManager, externalPlayer: Player) =
            player.position.isInterestedIn(externalPlayer.pos)
                || im.fieldIds[externalPlayer.index] != externalPlayer.pos.regionId

        fun updateField(buf: BitBuf, externalPlayer: Player) {
            val lastFieldId = im.fieldIds[externalPlayer.index]
            val lastFieldY = lastFieldId and 0xFF
            val lastFieldX = (lastFieldId shr 8) and 0xFF
            val lastFieldZ = lastFieldId shr 16

            val curentFieldId = externalPlayer.pos.regionId
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
           im.fieldIds[externalPlayer.index] = curentFieldId
        }

        fun updateExternalPlayer(buf: BitBuf, maskBuf: ByteBuf, externalPlayer: Player) {
            if (im.position.isInterestedIn(externalPlayer.pos)) {
                buf.writeBits(value = 0, amount = 2)
                if (im.fieldIds[externalPlayer.index] != externalPlayer.pos.regionId) {
                    buf.writeBoolean(true)
                    updateField(buf, externalPlayer)
                } else {
                    buf.writeBoolean(false)
                }
                buf.writeBits(value = externalPlayer.pos.x.value, amount = 13)
                buf.writeBits(value = externalPlayer.pos.y.value, amount = 13)
                buf.writeBoolean(true)
                updateLocalPlayerVisual(externalPlayer.visualManager, maskBuf, sortedSetOf(appearance, orientation, movementCached))
                im.localPlayers[externalPlayer.index] = externalPlayer
            } else {
                updateField(buf, externalPlayer)
            }
        }

        fun skipExternalPlayers(buf: BitBuf, currentIndex: Int, nsn: Boolean) {
            for (i in (currentIndex + 1) until im.externalPlayerCount) {
                val externalPlayerIndex = im.externalPlayerIndexes[i]
                if (hasBeenSkippedLastTick(externalPlayerIndex, nsn)) {
                    val nextPlayer = worldPlayers[externalPlayerIndex]
                    val requiresFlagUpdates = nextPlayer != null && externalUpdateRequired(im, nextPlayer)
                    if (requiresFlagUpdates) {
                        break
                    }
                    skip++
                }
            }
            buf.writeSkip(skip)
        }

        skip = 0
        for (i in 0 until im.externalPlayerCount) {
            val externalPlayerIndex = im.externalPlayerIndexes[i]
            if (hasBeenSkippedLastTick(externalPlayerIndex, nsn)) {
                if (skip > 0) {
                    skip--
                    markPlayerAsSkipped(externalPlayerIndex)
                } else {
                    val externalPlayer = worldPlayers[externalPlayerIndex]
                    val updateRequired = externalPlayer != null && externalUpdateRequired(im, externalPlayer)
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
        val dx = localPlayer.pos.x - im.lastPostion.x
        val dy = localPlayer.pos.y - im.lastPostion.y
        return getDirectionType(dx.value, dy.value)
    }

    private fun getDirectionType(dx: Int, dy: Int) = MOVEMENT[2 - dy][dx + 2]

    private fun markPlayerAsSkipped(playerIndex: Int) {
       im.skipFlags[playerIndex] = (im.skipFlags[playerIndex].toInt() or 0x2).toByte()
    }

    private fun hasBeenSkippedLastTick(playerIndex: Int, nsn: Boolean) = if (nsn) {
        im.skipFlags[playerIndex].toInt() and 0x1 == 0
    } else {
        im.skipFlags[playerIndex].toInt() and 0x1 != 0
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
        im: PlayerManager,
        maskBuf: ByteBuf,
        privateUpdates: SortedSet<UpdateType> = sortedSetOf()
    ) {
        var mask = 0
        privateUpdates.addAll(im.updateFlags)
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
            updateType.encode(maskBuf, im)
        }
    }

    class UpdateType(
        priority: Int,
        mask: Int,
        val encode: ByteBuf.(im: PlayerManager) -> Unit
    ) : CharacterVisual.UpdateType(priority, mask)

    companion object {
        private val INTEREST_SIZE = 32.tiles

        private val INTEREST_RANGE = INTEREST_SIZE / 2.tiles

        private fun Tile.isInterestedIn(other: Tile) = withInDistanceOf(other, INTEREST_RANGE)

        private val MOVEMENT = arrayOf(
            intArrayOf(11, 12, 13, 14, 15),
            intArrayOf(9, 5, 6, 7, 10),
            intArrayOf(7, 3, -1, 4, 8),
            intArrayOf(5, 0, 1, 2, 6),
            intArrayOf(0, 1, 2, 3, 4)
        )

        val movementTemporary = UpdateType(8, 0x200) { im ->
            writeByteNEG(if(im.movementType == CharacterVisual.MovementUpdateType.TELEPORT) 127 else 0)
        }

        val shout = UpdateType(3, 0x10) { im ->
            writeStringCP1252(im.shoutMessage!!) // TODO
        }

        val spotAnimation = UpdateType(10, 0x400) { im ->
            writeShortLEADD(im.spotAnimation?.id ?: 65535)
            writeInt(((im.spotAnimation?.height ?: 0) shl 16) or (im.spotAnimation?.delay ?:0))
        }

        val nameModifiers = UpdateType(12, 0x1000) { im ->
            im.nameModifiers.forEach { entry ->
                writeStringCP1252(entry)
            }
        }

        val sequence = UpdateType(2, 0x4) { im ->
            writeShort(im.sequence?.id ?: 65535)
            writeByteADD(0)
        }

        val chat = UpdateType(1, 0x80) { im ->
            writeShortLEADD((im.publicMessage!!.color shl 8) or im.publicMessage!!.effect)
            writeByteSUB(im.rights)
            writeByte(0) // some boolean
            val compressed = Unpooled.compositeBuffer(2).apply {
                addComponents(true,
                    Unpooled.buffer(2).apply { writeSmallSmart(im.publicMessage!!.length) },
                    Unpooled.wrappedBuffer(Huffman.compress(im.publicMessage!!.message))
                )
            }
            writeByte(compressed.readableBytes())
            writeBytesReversedADD(compressed)
        }

        val movementCached = UpdateType(4, 0x800) { im ->
            writeByteADD(if(im.inRunMode) 2 else 1)
        }

        val hit = UpdateType(11, 0x1) { im ->
            //TODO
        }

        val movementForced = UpdateType(7, 0x100) { im ->
            //TODO
        }

        val lockTurnToCharacter = UpdateType(9, 0x40) { im ->
            if(im.interacting == null) {
                writeShort(65535)
            } else {
                im.interacting?.let {
                    writeShort(im.index + 32768)
                }
            }
        }

        val appearance = UpdateType(6, 0x8) { im ->
            val lengthIndex = writerIndex()
            writeByte(0) //place holder for length
            writeByte(im.gender.opcode)
            writeByte(if(im.isSkulled) 1 else -1)
            writeByte(im.prayerIcon)
            im.equipment.head?.let { // write head gear
                writeShort(512 + it.id)
            } ?: run { writeByte(0) }
            im.equipment.cape?.let {  // write cape
                writeShort(512 + it.id)
            } ?: run { writeByte(0) }
            im.equipment.neck?.let {  // write neck gear
                writeShort(512 + it.id)
            } ?: run { writeByte(0) }
            im.equipment.weapon?.let { // write weapon
                writeShort(512 + it.id)
            } ?: run { writeByte(0) }
            im.equipment.body?.let { // write body
                writeShort(512 + it.id)
            } ?: run { writeShort(256 + im.style.torso) }
            im.equipment.shield?.let {  // write shield gear
                writeShort(512 + it.id)
            } ?: run { writeByte(0) }
            im.equipment.body?.let { // write arms
                if(it.isFullBody) writeByte(0) else writeShort(256 + im.style.arms)
            } ?: run { writeShort(256 + im.style.arms) }
            im.equipment.legs?.let { // write legs
                writeShort(512 + it.id)
            } ?: run { writeShort(256 + im.style.legs) }
            im.equipment.head?.let { // write hair
                if(it.coversHair) writeByte(0) else writeShort(256 + im.style.hair)
            } ?: run { writeShort(256 + im.style.hair) }
            im.equipment.hands?.let {  // write hands
                writeShort(512 + it.id)
            } ?: run { writeShort(256 + im.style.hands) }
            im.equipment.feet?.let { // write feet
                writeShort(512 + it.id)
            } ?: run { writeShort(256 + im.style.feet)}
            if(im.gender == PlayerManager.Gender.MALE) { //write beard
                im.equipment.head?.let {
                    if(it.coversFace) writeByte(0) else writeShort(256 + im.style.beard)
                } ?: run { writeShort(256 + im.style.beard) }
            } else {
                writeByte(0)
            }
            writeByte(im.colours.hair)
            writeByte(im.colours.torso)
            writeByte(im.colours.legs)
            writeByte(im.colours.feet)
            writeByte(im.colours.skin)

            writeShort(im.animations.stand)
            writeShort(im.animations.turn)
            writeShort(im.animations.walk)
            writeShort(im.animations.turn180)
            writeShort(im.animations.turn90CW)
            writeShort(im.animations.turn90CCW)
            writeShort(im.animations.run)
            writeStringCP1252(im.username) // username
            writeByte(im.combatLevel) // combat level
            writeShort(0) // skillId level
            writeByte(0) // hidden
            setByteNEG(lengthIndex, writerIndex() - lengthIndex - 1)
        }

        val orientation = UpdateType(5, 0x20) { player ->
            writeShortLEADD(player.orientation)
        }
    }
}