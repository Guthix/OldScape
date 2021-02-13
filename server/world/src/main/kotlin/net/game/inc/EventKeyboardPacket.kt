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