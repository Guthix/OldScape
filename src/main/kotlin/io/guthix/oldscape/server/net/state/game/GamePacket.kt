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
package io.guthix.oldscape.server.net.state.game

import io.github.classgraph.ClassGraph
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.event.GameEvent
import io.guthix.oldscape.server.event.Script
import io.guthix.oldscape.server.net.IncPacket
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.player.Player
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

class GamePacket(val opcode: Int, val type: PacketSize, val payload: ByteBuf)

sealed class PacketSize

class FixedSize(val size: Int) : PacketSize()

object VarByteSize : PacketSize()

object VarShortSize : PacketSize()

interface IncGamePacket : IncPacket {
    fun toEvent() : GameEvent
}

abstract class OutGameEvent {
    abstract val opcode: Int

    abstract val size: PacketSize

    abstract fun encode(ctx: ChannelHandlerContext): ByteBuf

    fun toPacket(ctx: ChannelHandlerContext) = GamePacket(opcode, size, encode(ctx))
}

abstract class GamePacketDecoder(val opcode: Int, val packetSize: PacketSize) {
    abstract fun decode(data: ByteBuf, ctx: ChannelHandlerContext): GameEvent

    companion object {
        private const val pkg = "io.guthix.oldscape.server.net.state.game.inp"

        val inc = mutableMapOf<Int, GamePacketDecoder>()

        fun loadIncPackets() {
            ClassGraph().whitelistPackages(pkg).scan().use { scanResult ->
                val pluginClassList = scanResult.getSubclasses(
                    "io.guthix.oldscape.server.net.state.game.GamePacketDecoder"
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