/**
 * This file is part of Guthix OldScape-Server.
 *
 * Guthix OldScape-Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.net.game

import io.github.classgraph.ClassGraph
import io.guthix.oldscape.server.dimensions.TileUnit
import io.guthix.oldscape.server.event.PlayerGameEvent
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Player
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

class GamePacket(val opcode: Int, val type: PacketSize, val payload: ByteBuf)

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

    companion object {
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