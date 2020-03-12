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

import io.guthix.oldscape.server.net.state.game.ClientEvent
import io.guthix.oldscape.server.event.KeyPress
import io.guthix.oldscape.server.event.KeyboardKey
import io.guthix.oldscape.server.event.KeyboardPressEvent
import io.guthix.oldscape.server.net.state.game.GamePacketDecoder
import io.guthix.oldscape.server.net.state.game.VarShortSize
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class EventKeyboardPacket : GamePacketDecoder(8, VarShortSize) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): ClientEvent {
        val keyPresses = mutableListOf<KeyPress>()
        for (button in 0 until (size / 4)) {
            val keyCode = data.readUnsignedByte().toInt()
            val timeInterval = data.readUnsignedMedium()
            keyPresses.add(KeyPress(KeyboardKey.get(keyCode), timeInterval))
        }
        return KeyboardPressEvent(keyPresses.toList())
    }

}