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
package io.guthix.oldscape.server.net.game.inc

import io.guthix.buffer.*
import io.guthix.oldscape.server.event.InventoryClickClientEvent
import io.guthix.oldscape.server.net.game.ClientEvent
import io.guthix.oldscape.server.net.game.FixedSize
import io.guthix.oldscape.server.net.game.GamePacketDecoder
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class Opheld1Packet : GamePacketDecoder(97, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val inventorySlotId = data.readUnsignedShortLE()
        val bitpack = data.readIntLE()
        val itemId = data.readUnsignedShort()
        return InventoryClickClientEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, itemId, inventorySlotId, 1)
    }
}

class Opheld2Packet : GamePacketDecoder(58, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val bitpack = data.readInt()
        val inventorySlotId = data.readUnsignedShortADD()
        val itemId = data.readUnsignedShortLEADD()
        return InventoryClickClientEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, itemId, inventorySlotId, 2)
    }
}

class Opheld3Packet : GamePacketDecoder(61, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val bitpack = data.readInt()
        val inventorySlotId = data.readUnsignedShortLE()
        val itemId = data.readUnsignedShort()
        return InventoryClickClientEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, itemId, inventorySlotId, 3)
    }
}

class Opheld4Packet : GamePacketDecoder(13, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val itemId = data.readUnsignedShortLE()
        val bitpack = data.readIntLE()
        val inventorySlotId = data.readUnsignedShortLE()
        return InventoryClickClientEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, itemId, inventorySlotId, 4)
    }
}

class Opheld5Packet : GamePacketDecoder(5, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val itemId = data.readUnsignedShortLEADD()
        val bitpack = data.readIntME()
        val inventorySlotId = data.readUnsignedShortADD()
        return InventoryClickClientEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, itemId, inventorySlotId, 5)
    }
}