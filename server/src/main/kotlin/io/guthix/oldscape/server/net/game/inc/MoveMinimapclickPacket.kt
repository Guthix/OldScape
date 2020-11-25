/*
 * Copyright 2018-2020 Guthix
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

import io.guthix.oldscape.server.event.MiniMapClickEvent
import io.guthix.oldscape.server.net.game.GamePacketDecoder
import io.guthix.oldscape.server.net.game.VarByteSize
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Player
import io.guthix.oldscape.server.world.map.dim.tiles
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class MoveMinimapclickPacket : GamePacketDecoder(87, VarByteSize) {
    override fun decode(
        buf: ByteBuf,
        size: Int,
        ctx: ChannelHandlerContext,
        player: Player,
        world: World
    ): MiniMapClickEvent {
        val x = buf.readUnsignedShort().tiles
        val type = buf.readByte().toInt()
        val y = buf.readUnsignedShort().tiles
        val mouseDx = buf.readUnsignedByte().toInt()
        val mouseDy = buf.readUnsignedByte().toInt()
        val angle = buf.readShort().toInt()
        buf.skipBytes(4)
        val playerX = buf.readUnsignedShort().tiles
        val playerY = buf.readUnsignedShort().tiles
        buf.skipBytes(1)
        return MiniMapClickEvent(x, y, type, mouseDx, mouseDy, angle, playerX, playerY, player, world)
    }
}