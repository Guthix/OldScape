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
package io.guthix.oldscape.server.net.state.game.inp

import io.guthix.buffer.*
import io.guthix.oldscape.server.event.InventoryClickClientEvent
import io.guthix.oldscape.server.net.state.game.ClientEvent
import io.guthix.oldscape.server.net.state.game.FixedSize
import io.guthix.oldscape.server.net.state.game.GamePacketDecoder
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class Opheld1Packet : GamePacketDecoder(82, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val bitpack = data.readIntIME()
        val itemId = data.readUnsignedShortLEADD()
        val inventorySlotId = data.readUnsignedShortADD()
        return InventoryClickClientEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, itemId, inventorySlotId, 1)
    }
}

class Opheld2Packet : GamePacketDecoder(58, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val bitpack = data.readInt()
        val itemId = data.readUnsignedShortLEADD()
        val inventorySlotId = data.readUnsignedShortADD()
        return InventoryClickClientEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, itemId, inventorySlotId, 2)
    }
}

class Opheld3Packet : GamePacketDecoder(44, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val itemId = data.readUnsignedShort()
        val bitpack = data.readIntME()
        val inventorySlotId = data.readUnsignedShortADD()
        return InventoryClickClientEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, itemId, inventorySlotId, 3)
    }
}

class Opheld4Packet : GamePacketDecoder(74, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val bitpack = data.readIntIME()
        val itemId = data.readUnsignedShortLE()
        val inventorySlotId = data.readUnsignedShort()
        return InventoryClickClientEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, itemId, inventorySlotId, 4)
    }
}

class Opheld5Packet : GamePacketDecoder(40, FixedSize(8)) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val bitpack = data.readIntIME()
        val itemId = data.readUnsignedShortLE()
        val inventorySlotId = data.readUnsignedShortADD()
        return InventoryClickClientEvent(bitpack shr Short.SIZE_BITS, bitpack and 0xFFFF, itemId, inventorySlotId, 5)
    }
}