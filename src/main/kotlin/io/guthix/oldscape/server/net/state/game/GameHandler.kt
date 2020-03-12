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
package io.guthix.oldscape.server.net.state.game

import io.guthix.oldscape.server.event.script.EventBus
import io.guthix.oldscape.server.net.PacketInboundHandler
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.character.player.Player
import io.netty.channel.ChannelHandlerContext

class GameHandler(val world: World, val player: Player) : PacketInboundHandler<ClientEvent>() {
    override fun channelRegistered(ctx: ChannelHandlerContext) {
        super.channelRegistered(ctx)
        player.ctx = ctx
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: ClientEvent) {
        EventBus.schedule(msg.toGameEvent(world), world, player)
    }
}