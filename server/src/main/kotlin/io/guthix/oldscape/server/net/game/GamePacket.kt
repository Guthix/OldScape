/*
 * Copyright 2018-2021 Guthix
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
package io.guthix.oldscape.server.net.game

import io.github.classgraph.ClassGraph
import io.guthix.oldscape.server.event.PlayerGameEvent
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.map.dim.TileUnit
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufHolder
import io.netty.channel.ChannelHandlerContext
import mu.KLogging

class GamePacket(val opcode: Int, val type: PacketSize, val payload: ByteBuf) : ByteBufHolder {
    override fun refCnt(): Int = payload.refCnt()

    override fun release(): Boolean = payload.release()

    override fun release(decrement: Int): Boolean = payload.release(decrement)

    override fun content(): ByteBuf = payload

    override fun copy(): ByteBufHolder = GamePacket(opcode, type, payload.copy())

    override fun duplicate(): ByteBufHolder = GamePacket(opcode, type, payload.duplicate())

    override fun retainedDuplicate(): ByteBufHolder = GamePacket(opcode, type, payload.retainedDuplicate())

    override fun replace(content: ByteBuf): ByteBufHolder = GamePacket(opcode, type, content)

    override fun retain(): ByteBufHolder {
        payload.retain()
        return this
    }

    override fun retain(increment: Int): ByteBufHolder {
        payload.retain(increment)
        return this
    }

    override fun touch(): ByteBufHolder {
        payload.touch()
        return this
    }

    override fun touch(hint: Any): ByteBufHolder {
        payload.touch(hint)
        return this
    }
}

sealed class PacketSize

class FixedSize(val size: Int) : PacketSize()

object VarByteSize : PacketSize()

object VarShortSize : PacketSize()

abstract class ZoneOutGameEvent(
    private val localX: TileUnit,
    private val localY: TileUnit
) : OutGameEvent {
    val posBitPack: Int get() = (localX.value shl 4) or (localY.value and 7)

    abstract val enclOpcode: Int
}

interface OutGameEvent {
    val opcode: Int

    val size: PacketSize

    fun encode(ctx: ChannelHandlerContext): ByteBuf

    fun toPacket(ctx: ChannelHandlerContext): GamePacket = GamePacket(opcode, size, encode(ctx))
}

abstract class GamePacketDecoder(val opcode: Int, val packetSize: PacketSize) {
    abstract fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent

    companion object : KLogging() {
        private const val pkg = "io.guthix.oldscape.server.net.game.inc"

        val inc: MutableMap<Int, GamePacketDecoder> = mutableMapOf()

        fun loadIncPackets() {
            ClassGraph().whitelistPackages(pkg).scan().use { scanResult ->
                val pluginClassList = scanResult.getSubclasses(
                    "io.guthix.oldscape.server.net.game.GamePacketDecoder"
                ).directOnly()
                pluginClassList.forEach {
                    val clazz = it.loadClass(GamePacketDecoder::class.java).getDeclaredConstructor().newInstance()
                    inc[clazz.opcode] = clazz
                }
                logger.info { "Loaded ${inc.size} inc packet decoders" }
            }
        }
    }
}