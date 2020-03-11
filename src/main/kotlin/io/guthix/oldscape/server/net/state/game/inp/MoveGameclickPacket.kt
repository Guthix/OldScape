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

import io.guthix.buffer.readUnsignedByteADD
import io.guthix.oldscape.server.api.script.GameEvent
import io.guthix.oldscape.server.event.MapClickEvent
import io.guthix.oldscape.server.net.state.game.GamePacketDecoder
import io.guthix.oldscape.server.net.state.game.VarByteSize
import io.guthix.oldscape.server.world.mapsquare.zone.tile.tiles
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class MoveGameclickPacket : GamePacketDecoder(64, VarByteSize) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): GameEvent {
        val x = data.readUnsignedShort()
        val type = data.readUnsignedByteADD().toInt()
        val y = data.readUnsignedShort()
        return MapClickEvent(x.tiles, y.tiles, type)
    }
}