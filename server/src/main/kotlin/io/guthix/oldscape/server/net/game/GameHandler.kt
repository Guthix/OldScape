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
package io.guthix.oldscape.server.net.game

import io.guthix.oldscape.server.event.ClientDisconnectEvent
import io.guthix.oldscape.server.event.EventBus
import io.guthix.oldscape.server.event.PlayerGameEvent
import io.guthix.oldscape.server.net.PacketInboundHandler
import io.guthix.oldscape.server.world.World
import io.guthix.oldscape.server.world.entity.Player
import io.netty.channel.ChannelHandlerContext

class GameHandler(val player: Player, val world: World) : PacketInboundHandler<PlayerGameEvent>() {
    override fun channelRegistered(ctx: ChannelHandlerContext) {
        super.channelRegistered(ctx)
        player.ctx = ctx
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: PlayerGameEvent) {
        EventBus.schedule(msg)
    }

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        super.channelInactive(ctx)
        EventBus.schedule(ClientDisconnectEvent(player, world))
    }
}