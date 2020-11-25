/*
 * Copyright 2018-2020 Guthix
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.guthix.oldscape.server.net.game.out

import io.guthix.buffer.*
import io.guthix.oldscape.server.net.Huffman
import io.guthix.oldscape.server.net.game.OutGameEvent
import io.guthix.oldscape.server.net.game.VarShortSize
import io.guthix.oldscape.server.world.PlayerList
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Npc
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.entity.interest.MovementInterestUpdate
import io.guthix.oldscape.server.world.entity.interest.PlayerManager
import io.guthix.oldscape.server.world.entity.interest.PlayerUpdateType
import io.guthix.oldscape.server.world.entity.interest.regionId
import io.guthix.oldscape.server.world.map.Tile
import io.guthix.oldscape.server.world.map.dim.floors
import io.guthix.oldscape.server.world.map.dim.tiles
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import java.util.*
import kotlin.math.abs

class PlayerInfoPacket(
    private val worldPlayers: PlayerList,
    private val im: PlayerManager,
    private val player: Player
) : OutGameEvent, CharacterInfoPacket() {
    override val opcode: Int = 79

    override val size: VarShortSize = VarShortSize

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
        fun localUpdateRequired(thisPlayer: Player, localPlayer: Player) = (localPlayer.updateFlags.isNotEmpty()
            || !thisPlayer.pos.isInterestedIn(localPlayer.pos)
            || localPlayer.movementType != MovementInterestUpdate.STAY
            || localPlayer.isLoggingOut
            )

        fun updateLocalPlayer(localPlayer: Player, bitBuf: BitBuf, maskBuf: ByteBuf) {
            val flagUpdateRequired = localPlayer.updateFlags.isNotEmpty()
            bitBuf.writeBoolean(flagUpdateRequired)
            if (localPlayer.movementType == MovementInterestUpdate.TELEPORT) {
                bitBuf.writeBits(value = 3, amount = 2)
                var localPlayerOutsideView = !player.pos.isInterestedIn(localPlayer.pos)
                if (im.index == localPlayer.index) localPlayerOutsideView = true
                bitBuf.writeBoolean(localPlayerOutsideView)
                var dx = localPlayer.pos.x - player.lastPos.x // TODO maybe this is broken
                var dy = localPlayer.pos.y - player.lastPos.y
                if (localPlayerOutsideView) {
                    buf.writeBits(
                        ((localPlayer.pos.floor.value and 0x3) shl 28) or
                            ((dx.value and 0x3fff) shl 14) or
                            (dy.value and 0x3fff), 30
                    )
                } else {
                    if (dx < 0.tiles) dx += INTEREST_SIZE
                    if (dy < 0.tiles) dy += INTEREST_SIZE
                    buf.writeBits(
                        ((localPlayer.pos.floor.value and 0x3) shl 10) or
                            ((dx.value and 0x1F) shl 5) or
                            (dy.value and 0x1F), 12
                    )
                }
            } else if (!player.pos.isInterestedIn(localPlayer.pos) || localPlayer.isLoggingOut) {
                bitBuf.writeBits(value = 0, amount = 2)
                im.localPlayers[localPlayer.index] = null
            } else if (localPlayer.movementType == MovementInterestUpdate.WALK) {
                bitBuf.writeBits(value = 1, amount = 2)
                bitBuf.writeBits(value = getDirectionWalk(localPlayer), amount = 3)
            } else if (localPlayer.movementType == MovementInterestUpdate.RUN) {
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
            ((currentIndex + 1) until im.localPlayerCount)
                .asSequence()
                .map { im.localPlayerIndexes[it] }
                .filter { hasBeenSkippedLastTick(it, nsn) }
                .map { im.localPlayers[it] }
                .map { it != null && localUpdateRequired(player, it) }
                .takeWhile { !it }
                .toList()
                .forEach { _ -> skip++ }
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

    fun updateField(buf: BitBuf, externalPlayer: Player) {
        val lastFieldId = im.regionIds[externalPlayer.index]
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
        if (dx == 0 && dy == 0) { // change floors
            buf.writeBits(value = 1, amount = 2)
            buf.writeBits(value = dz, amount = 2)
        } else if (abs(dx) <= 1 && abs(dy) <= 1) { // change regions & floors
            buf.writeBits(value = 2, amount = 2)
            buf.writeBits(value = (dz shl 3) or getDirectionType(dx, dy), amount = 5)
        } else { // new entry
            buf.writeBits(value = 3, amount = 2)
            buf.writeBits(value = Tile(dz.floors, dx.tiles, dy.tiles).regionId, amount = 18)
        }
        im.regionIds[externalPlayer.index] = curentFieldId
    }

    private fun processExternalPlayers(buf: BitBuf, maskBuf: ByteBuf, nsn: Boolean) {
        fun externalUpdateRequired(player: Player, externalPlayer: Player) =
            player.pos.isInterestedIn(externalPlayer.pos)
                || im.regionIds[externalPlayer.index] != externalPlayer.pos.regionId

        fun updateExternalPlayer(buf: BitBuf, maskBuf: ByteBuf, externalPlayer: Player) {
            if (player.pos.isInterestedIn(externalPlayer.pos)) {
                buf.writeBits(value = 0, amount = 2)
                if (im.regionIds[externalPlayer.index] != externalPlayer.pos.regionId) {
                    buf.writeBoolean(true)
                    updateField(buf, externalPlayer)
                } else {
                    buf.writeBoolean(false)
                }
                buf.writeBits(value = externalPlayer.pos.x.value, amount = 13)
                buf.writeBits(value = externalPlayer.pos.y.value, amount = 13)
                buf.writeBoolean(true)
                updateLocalPlayerVisual(externalPlayer, maskBuf, sortedSetOf(appearance, orientation, movementCached))
                im.localPlayers[externalPlayer.index] = externalPlayer
            } else {
                updateField(buf, externalPlayer)
            }
        }

        fun skipExternalPlayers(buf: BitBuf, currentIndex: Int, nsn: Boolean) {
            ((currentIndex + 1) until im.externalPlayerCount)
                .asSequence()
                .map { im.externalPlayerIndexes[it] }
                .filter { hasBeenSkippedLastTick(it, nsn) }
                .map { worldPlayers[it] }
                .map { it != null && externalUpdateRequired(player, it) }
                .takeWhile { !it }
                .toList()
                .forEach { _ -> skip++ }
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
                    val updateRequired = externalPlayer != null && externalUpdateRequired(
                        player, externalPlayer
                    )
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
        val dx = localPlayer.pos.x - localPlayer.lastPos.x
        val dy = localPlayer.pos.y - localPlayer.lastPos.y
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
        localPlayer: Player,
        maskBuf: ByteBuf,
        privateUpdates: SortedSet<PlayerUpdateType> = sortedSetOf()
    ) {
        var mask = 0
        privateUpdates.addAll(localPlayer.updateFlags)
        privateUpdates.forEach { update ->
            mask = mask or update.mask
        }
        if (mask >= 0xff) {
            maskBuf.writeByte(mask or 0x40)
            maskBuf.writeByte(mask shr 8)
        } else {
            maskBuf.writeByte(mask)
        }
        privateUpdates.forEach { updateType ->
            updateType.encode(maskBuf, localPlayer)
        }
    }

    companion object {
        private val MOVEMENT = arrayOf(
            intArrayOf(11, 12, 13, 14, 15),
            intArrayOf(9, 5, 6, 7, 10),
            intArrayOf(7, 3, -1, 4, 8),
            intArrayOf(5, 0, 1, 2, 6),
            intArrayOf(0, 1, 2, 3, 4)
        )

        val movementForced: PlayerUpdateType = PlayerUpdateType(0, 0x200) { player ->
            //TODO
        }

        val spotAnimation: PlayerUpdateType = PlayerUpdateType(1, 0x800) { player ->
            writeShortAdd(player.spotAnimation?.id ?: 65535)
            writeIntLE(((player.spotAnimation?.height ?: 0) shl 16) or (player.spotAnimation?.delay ?: 0))
        }

        val sequence: PlayerUpdateType = PlayerUpdateType(2, 0x80) { player ->
            writeShortLE(player.sequence?.id ?: 65535)
            writeByteNeg(player.sequence?.duration ?: 0)
        }

        val appearance: PlayerUpdateType = PlayerUpdateType(3, 0x2) { player ->
            val tempBuf = Unpooled.buffer() // TODO use pooling
            tempBuf.writeByte(player.gender.opcode)
            tempBuf.writeByte(if (player.isSkulled) 1 else -1)
            tempBuf.writeByte(player.prayerIcon)
            player.equipmentSet.head?.let { // write head gear
                tempBuf.writeShort(512 + it.id)
            } ?: run { tempBuf.writeByte(0) }
            player.equipmentSet.cape?.let {  // write cape
                tempBuf.writeShort(512 + it.id)
            } ?: run { tempBuf.writeByte(0) }
            player.equipmentSet.neck?.let {  // write neck gear
                tempBuf.writeShort(512 + it.id)
            } ?: run { tempBuf.writeByte(0) }
            player.equipmentSet.weapon?.let { // write weapon
                tempBuf.writeShort(512 + it.id)
            } ?: run { tempBuf.writeByte(0) }
            player.equipmentSet.body?.let { // write body
                tempBuf.writeShort(512 + it.id)
            } ?: run { tempBuf.writeShort(256 + player.style.torso) }
            player.equipmentSet.shield?.let {  // write shield gear
                tempBuf.writeShort(512 + it.id)
            } ?: run { tempBuf.writeByte(0) }
            player.equipmentSet.body?.let { // write arms
                if (player.equipmentSet.isFullBody) {
                    tempBuf.writeByte(0)
                } else tempBuf.writeShort(256 + player.style.arms)
            } ?: run { tempBuf.writeShort(256 + player.style.arms) }
            player.equipmentSet.legs?.let { // write legs
                tempBuf.writeShort(512 + it.id)
            } ?: run { tempBuf.writeShort(256 + player.style.legs) }
            player.equipmentSet.head?.let { // write hair
                if (player.equipmentSet.coversHair) {
                    tempBuf.writeByte(0)
                } else tempBuf.writeShort(256 + player.style.hair)
            } ?: run { tempBuf.writeShort(256 + player.style.hair) }
            player.equipmentSet.hands?.let {  // write hands
                tempBuf.writeShort(512 + it.id)
            } ?: run { tempBuf.writeShort(256 + player.style.hands) }
            player.equipmentSet.feet?.let { // write feet
                tempBuf.writeShort(512 + it.id)
            } ?: run { tempBuf.writeShort(256 + player.style.feet) }
            if (player.gender == PlayerManager.Gender.MALE) { //tBuf.write beard
                player.equipmentSet.head?.let {
                    if (player.equipmentSet.coversFace) {
                        tempBuf.writeByte(0)
                    } else tempBuf.writeShort(256 + player.style.beard)
                } ?: run { tempBuf.writeShort(256 + player.style.beard) }
            } else {
                tempBuf.writeByte(0)
            }
            tempBuf.writeByte(player.colours.hair)
            tempBuf.writeByte(player.colours.torso)
            tempBuf.writeByte(player.colours.legs)
            tempBuf.writeByte(player.colours.feet)
            tempBuf.writeByte(player.colours.skin)

            tempBuf.writeShort(player.animations.stand)
            tempBuf.writeShort(player.animations.turn)
            tempBuf.writeShort(player.animations.walk)
            tempBuf.writeShort(player.animations.turn180)
            tempBuf.writeShort(player.animations.turn90CW)
            tempBuf.writeShort(player.animations.turn90CCW)
            tempBuf.writeShort(player.animations.run)
            tempBuf.writeStringCP1252(player.username) // username
            tempBuf.writeByte(player.combatLevel) // combat level
            tempBuf.writeShort(0) // skillId level
            tempBuf.writeByte(0) // hidden
            writeByte(tempBuf.writerIndex())
            writeBytesReversedAdd(tempBuf)
        }

        val shout: PlayerUpdateType = PlayerUpdateType(4, 0x20) { player ->
            writeStringCP1252(player.shoutMessage!!) // TODO
        }

        val lockTurnTo: PlayerUpdateType = PlayerUpdateType(5, 0x4) { player ->
            val index = when (val interacting = player.interacting) {
                is Npc -> interacting.index
                is Player -> interacting.index + 32768
                else -> 65535
            }
            writeShortAddLE(index)
        }

        val movementCached: PlayerUpdateType = PlayerUpdateType(6, 0x1000) { player ->
            writeByteNeg(if (player.inRunMode) 2 else 1)
        }

        val chat: PlayerUpdateType = PlayerUpdateType(7, 0x1) { player ->
            writeShortAdd((player.publicMessage!!.color shl 8) or player.publicMessage!!.effect)
            writeByteNeg(player.rights)
            writeByteNeg(0) // some boolean
            val compressed = Unpooled.compositeBuffer(2).apply {
                addComponents(true,
                    Unpooled.buffer(2).apply { writeSmallSmart(player.publicMessage!!.length) },
                    Unpooled.wrappedBuffer(Huffman.compress(player.publicMessage!!.message))
                )
            }
            writeByteAdd(compressed.readableBytes())
            writeBytes(compressed)
        }


        val nameModifiers: PlayerUpdateType = PlayerUpdateType(8, 0x100) { player ->
            player.nameModifiers.forEach { entry ->
                writeStringCP1252(entry)
            }
        }

        val hit: PlayerUpdateType = PlayerUpdateType(9, 0x10) { player ->
            writeByte(player.hitMarkQueue.size)
            player.hitMarkQueue.forEach { hitMark ->
                writeSmallSmart(hitMark.color.id)
                writeSmallSmart(hitMark.damage)
                writeSmallSmart(hitMark.delay)
            }
            writeByte(player.healthBarQueue.size)
            player.healthBarQueue.forEach { healthBar ->
                writeSmallSmart(healthBar.id)
                writeSmallSmart(healthBar.decreaseSpeed)
                writeSmallSmart(healthBar.delay)
                writeByte(healthBar.amount)
            }
        }

        val movementTemporary: PlayerUpdateType = PlayerUpdateType(10, 0x400) { player ->
            writeByteNeg(if (player.movementType == MovementInterestUpdate.TELEPORT) 127 else 0)
        }

        val orientation: PlayerUpdateType = PlayerUpdateType(11, 0x8) { player ->
            writeShortAdd(player.orientation)
        }
    }
}