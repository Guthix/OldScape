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
package io.guthix.oldscape.server.net.game

import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.event.PlayerGameEvent
import io.guthix.oldscape.server.net.PacketInboundHandler
import io.guthix.oldscape.server.world.entity.Player
import io.netty.channel.ChannelHandlerContext

class GameHandler(val player: Player) : PacketInboundHandler<PlayerGameEvent>() {
    override fun channelRegistered(ctx: ChannelHandlerContext) {
        super.channelRegistered(ctx)
        player.ctx = ctx
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: PlayerGameEvent) {
        EventBus.schedule(msg)
    }
}