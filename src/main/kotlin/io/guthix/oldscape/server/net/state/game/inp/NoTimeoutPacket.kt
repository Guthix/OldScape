package io.guthix.oldscape.server.net.state.game.inp

import io.guthix.oldscape.server.event.GameEvent
import io.guthix.oldscape.server.net.state.game.FixedSize
import io.guthix.oldscape.server.net.state.game.GamePacketDecoder
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

object KeepAliveEvent : GameEvent

class NoTimeOutPacketDecoder : GamePacketDecoder(0, FixedSize(0)) {
    override fun decode(data: ByteBuf, ctx: ChannelHandlerContext) = KeepAliveEvent
}