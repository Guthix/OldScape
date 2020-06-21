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
package io.guthix.oldscape.server.net.game.inc

import io.guthix.buffer.readIntME
import io.guthix.buffer.readUnsignedShortADD
import io.guthix.buffer.readUnsignedShortLEADD
import io.guthix.oldscape.server.event.InventoryObjectClickEvent
import io.guthix.oldscape.server.event.PlayerGameEvent
import io.guthix.oldscape.server.net.game.FixedSize
import io.guthix.oldscape.server.net.game.GamePacketDecoder
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Player
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class Opheld1Packet : GamePacketDecoder(97, FixedSize(8)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val inventorySlotId = buf.readUnsignedShortLE()
        val bitpack = buf.readIntLE()
        val itemId = buf.readUnsignedShort()
        return InventoryObjectClickEvent(
            bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, itemId, inventorySlotId, 1, player, world
        )
    }
}

class Opheld2Packet : GamePacketDecoder(58, FixedSize(8)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val bitpack = buf.readInt()
        val inventorySlotId = buf.readUnsignedShortADD()
        val itemId = buf.readUnsignedShortLEADD()
        return InventoryObjectClickEvent(
            bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, itemId, inventorySlotId, 2, player, world
        )
    }
}

class Opheld3Packet : GamePacketDecoder(61, FixedSize(8)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val bitpack = buf.readInt()
        val inventorySlotId = buf.readUnsignedShortLE()
        val itemId = buf.readUnsignedShort()
        return InventoryObjectClickEvent(
            bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, itemId, inventorySlotId, 3, player, world
        )
    }
}

class Opheld4Packet : GamePacketDecoder(13, FixedSize(8)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val itemId = buf.readUnsignedShortLE()
        val bitpack = buf.readIntLE()
        val inventorySlotId = buf.readUnsignedShortLE()
        return InventoryObjectClickEvent(
            bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, itemId, inventorySlotId, 4, player, world
        )
    }
}

class Opheld5Packet : GamePacketDecoder(5, FixedSize(8)) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val itemId = buf.readUnsignedShortLEADD()
        val bitpack = buf.readIntME()
        val inventorySlotId = buf.readUnsignedShortADD()
        return InventoryObjectClickEvent(
            bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, itemId, inventorySlotId, 5, player, world
        )
    }
}