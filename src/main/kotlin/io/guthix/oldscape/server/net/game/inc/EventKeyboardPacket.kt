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

import io.guthix.oldscape.server.event.KeyPress
import io.guthix.oldscape.server.event.KeyboardKey
import io.guthix.oldscape.server.event.KeyboardPressEvent
import io.guthix.oldscape.server.event.PlayerGameEvent
import io.guthix.oldscape.server.net.game.GamePacketDecoder
import io.guthix.oldscape.server.net.game.VarShortSize
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Player
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class EventKeyboardPacket : GamePacketDecoder(3, VarShortSize) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): PlayerGameEvent {
        val keyPresses = mutableListOf<KeyPress>()
        for (button in 0 until (size / 4)) {
            val timeInterval = buf.readUnsignedMediumLE()
            val keyCode = buf.readUnsignedByte().toInt()
            keyPresses.add(KeyPress(KeyboardKey.get(keyCode), timeInterval))
        }
        return KeyboardPressEvent(keyPresses.toList(), player, world)
    }

}