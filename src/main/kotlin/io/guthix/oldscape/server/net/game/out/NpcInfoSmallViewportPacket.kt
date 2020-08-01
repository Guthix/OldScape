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
import io.guthix.oldscape.server.dimensions.TileUnit
import io.guthix.oldscape.server.dimensions.tiles
import io.guthix.oldscape.server.net.game.OutGameEvent
import io.guthix.oldscape.server.net.game.VarShortSize
import io.guthix.oldscape.server.world.NpcList
import io.guthix.oldscape.server.world.entity.Npc
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.entity.interest.MovementInterestUpdate
import io.guthix.oldscape.server.world.entity.interest.NpcUpdateType
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class NpcInfoSmallViewportPacket(
    private val player: Player,
    private val npcs: NpcList
) : OutGameEvent, CharacterInfoPacket() {
    override val opcode: Int = 62

    override val size: VarShortSize = VarShortSize

    override fun encode(ctx: ChannelHandlerContext): ByteBuf {
        val buf = ctx.alloc().buffer()
        val bitBuf = buf.toBitMode()
        localNpcUpdate(bitBuf)
        externalNpcUpdate(bitBuf)
        val byteBuf = bitBuf.toByteMode()
        player.npcManager.localNpcs.filter { it.updateFlags.isNotEmpty() }.forEach { updateLocalNpcVisual(it, byteBuf) }
        return buf
    }

    fun localNpcUpdate(buf: BitBuf): BitBuf {
        buf.writeBits(value = player.npcManager.localNpcs.size, amount = 8)
        val removals = mutableListOf<Npc>()
        for (npc in player.npcManager.localNpcs) {
            when {
                npc.movementType == MovementInterestUpdate.WALK -> {
                    buf.writeBoolean(true)
                    buf.writeBits(value = 1, amount = 2)
                    buf.writeBits(value = getDirectionWalk(npc), amount = 3)
                    buf.writeBoolean(npc.updateFlags.isNotEmpty())
                }
                npc.movementType == MovementInterestUpdate.RUN -> {
                    buf.writeBoolean(true)
                    buf.writeBits(value = 2, amount = 2)
                    buf.writeBits(value = getDirectionWalk(npc), amount = 3)
                    buf.writeBits(value = getDirectionWalk(npc), amount = 3) //TODO Needs to send second step
                    buf.writeBoolean(npc.updateFlags.isNotEmpty())
                }
                npc.index == -1 || npc.movementType == MovementInterestUpdate.TELEPORT
                    || !player.pos.isInterestedIn(npc.pos) -> {
                    buf.writeBoolean(true)
                    buf.writeBits(value = 3, amount = 2)
                    removals.add(npc)
                }
                npc.updateFlags.isNotEmpty() -> {
                    buf.writeBoolean(true)
                    buf.writeBits(value = 0, amount = 2)
                }
                else -> buf.writeBoolean(false)
            }
        }
        player.npcManager.localNpcs.removeAll(removals) //TODO make this more efficient
        return buf
    }

    private fun needsAdd(npc: Npc) = player.pos.isInterestedIn(npc.pos) && !player.npcManager.localNpcs.contains(npc)

    fun getRespectiveLocation(npcTile: TileUnit, playerTile: TileUnit): TileUnit {
        var loc = npcTile - playerTile
        if (loc < 0.tiles) {
            loc += INTEREST_SIZE
        }
        return loc
    }

    fun externalNpcUpdate(buf: BitBuf): BitBuf {
        var npcsAdded = 0
        for (npc in npcs) { // TODO optimize and use surrounding npcs
            if (npcsAdded > 16) break
            if (needsAdd(npc)) {
                buf.writeBits(value = npc.index, amount = 15)
                buf.writeBits(value = getRespectiveLocation(npc.pos.y, player.pos.y).value, amount = 5)
                buf.writeBits(value = npc.orientation, amount = 3) //TODO fix rotation
                buf.writeBoolean(false) // Is teleport
                buf.writeBoolean(npc.updateFlags.isNotEmpty())
                buf.writeBits(value = getRespectiveLocation(npc.pos.x, player.pos.x).value, amount = 5)
                buf.writeBits(value = npc.id, amount = 14)

                player.npcManager.localNpcs.add(npc)
                npcsAdded++
            }
        }
        if (player.npcManager.localNpcs.any { it.updateFlags.isNotEmpty() }) {
            buf.writeBits(value = 32767, amount = 15)
        }
        return buf
    }

    private fun getDirectionWalk(localNpc: Npc): Int {
        val dx = localNpc.pos.x - localNpc.lastPos.x
        val dy = localNpc.pos.y - localNpc.lastPos.y
        return getDirectionType(dx, dy)
    }

    private fun getDirectionType(dx: TileUnit, dy: TileUnit) = movementOpcodes[1 - dy.value][dx.value + 1]

    private fun updateLocalNpcVisual(npc: Npc, maskBuf: ByteBuf) {
        var mask = 0
        npc.updateFlags.forEach { update ->
            mask = mask or update.mask
        }
        maskBuf.writeByte(mask)
        npc.updateFlags.forEach { updateType ->
            updateType.encode(maskBuf, npc)
        }
    }

    companion object {
        private val movementOpcodes = arrayOf(
            intArrayOf(0, 1, 2),
            intArrayOf(3, -1, 4),
            intArrayOf(5, 6, 7)
        )

        val sequence: NpcUpdateType = NpcUpdateType(0, 0x80) { npc ->
            writeShortAddLE(npc.sequence?.id ?: 65535)
            writeByte(npc.sequence?.duration ?: 0)
        }

        val orientation: NpcUpdateType = NpcUpdateType(1, 0x10) { npc ->
            //TODO
        }

        val transform: NpcUpdateType = NpcUpdateType(2, 0x20) { npc ->
            //TODO
        }

        val turnLockTo: NpcUpdateType = NpcUpdateType(3, 0x8) { npc ->
            val index = when (val interacting = npc.interacting) {
                is Npc -> interacting.index
                is Player -> interacting.index + 32768
                else -> 65535
            }
            writeShort(index)
        }

        val spotAnimation: NpcUpdateType = NpcUpdateType(4, 0x2) { npc ->
            //TODO
        }

        val shout: NpcUpdateType = NpcUpdateType(4, 0x40) { npc ->
            //TODO
        }

        val hit: NpcUpdateType = NpcUpdateType(5, 0x1) { npc ->
            writeByteSub(npc.hitMarkQueue.size)
            npc.hitMarkQueue.forEach { hitMark ->
                writeSmallSmart(hitMark.color.id)
                writeSmallSmart(hitMark.damage)
                writeSmallSmart(hitMark.delay)
            }
            writeByteNeg(npc.healthBarQueue.size)
            npc.healthBarQueue.forEach { healthBar ->
                writeSmallSmart(healthBar.id)
                writeSmallSmart(healthBar.decreaseSpeed)
                writeSmallSmart(healthBar.delay)
                writeByte(healthBar.amount)
            }
        }

        val forceMovement: NpcUpdateType = NpcUpdateType(6, 0x4) { npc ->
            //TODO
        }
    }
}