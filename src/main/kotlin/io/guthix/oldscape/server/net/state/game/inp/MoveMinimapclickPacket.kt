package io.guthix.oldscape.server.net.state.game.inp

import io.guthix.buffer.readByteADD
import io.guthix.oldscape.server.event.imp.MiniMapClickEvent
import io.guthix.oldscape.server.net.state.game.GamePacketDecoder
import io.guthix.oldscape.server.net.state.game.VarByteSize
import io.guthix.oldscape.server.world.mapsquare.zone.tile.tiles
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class MoveMinimapclickPacket : GamePacketDecoder(87, VarByteSize) {
    override fun decode(data: ByteBuf, size: Int, ctx: ChannelHandlerContext): MiniMapClickEvent {
        val x = data.readUnsignedShort()
        val type = data.readByteADD().toInt()
        val y = data.readUnsignedShort()
        val mouseDx = data.readUnsignedByte().toInt()
        val mouseDy = data.readUnsignedByte().toInt()
        val angle = data.readShort().toInt()
        data.skipBytes(4)
        val playerX = data.readUnsignedShort()
        val playerY = data.readUnsignedShort()
        data.skipBytes(1)
        return MiniMapClickEvent(x.tiles, y.tiles, type, mouseDx, mouseDy, angle, playerX.tiles, playerY.tiles)
    }
}